package mainproject.stocksite.domain.stock.overall.kospi.cache.updater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.kospi.dto.KospiStockDto;
import mainproject.stocksite.domain.stock.overall.util.DateUtils;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
import mainproject.stocksite.global.exception.BusinessLogicException;
import mainproject.stocksite.global.exception.ExceptionCode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kospi.cache.updater
 * FileName: KospiStockIndexUpdater
 * Author: bangjaeyoung
 * Date: 2024-01-24
 * Description: KOSPI 주가지수시세 Open API 호출 / 캐시 데이터 저장(스케쥴링)
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KospiStockIndexUpdater {
    public static final String KOSPI_STOCK_INDEX_CACHE_KEY = "KOSPIStockIndices: ";
    private static final String CRON_EXPRESSION = "0 0 13 * * *";   // 오후 1시
    private static final String TIME_ZONE = "Asia/Seoul";
    private static final String KOSPI_STOCK_INDEX_API_URL = "http://apis.data.go.kr/1160100/service/GetMarketIndexInfoService/getStockMarketIndex";
    private static final int NUM_OF_ROWS = 5;
    private static final int PAGE_NO = 1;
    
    private final RestTemplate restTemplate;
    private final OpenApiSecretInfo openApiSecretInfo;
    private final RedisTemplate<String, List<KospiStockDto.Index>> redisTemplate;
    
    @PostConstruct
    @Scheduled(cron = CRON_EXPRESSION, zone = TIME_ZONE)
    public List<KospiStockDto.Index> updateKospiStockIndices() {
        String responseData = requestToOpenApiServer();
        List<KospiStockDto.Index> indexDtos = transformDataToDto(responseData);
        
        if (indexDtos == null) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOUND_STOCK_DATA);
        }
        
        redisTemplate.opsForValue()
                .set(
                        KOSPI_STOCK_INDEX_CACHE_KEY,
                        indexDtos,
                        24,
                        TimeUnit.HOURS
                );
        
        return indexDtos;
    }
    
    private String requestToOpenApiServer() {
        String requestUrl = buildApiUrl();
        return restTemplate.getForEntity(requestUrl, String.class).getBody();
    }
    
    private String buildApiUrl() {
        return UriComponentsBuilder.fromHttpUrl(KOSPI_STOCK_INDEX_API_URL)
                .queryParam("serviceKey", openApiSecretInfo.getServiceKey())
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", PAGE_NO)
                .queryParam("resultType", "json")
                .queryParam("beginBasDt", DateUtils.getFiveDaysAgoToNow())
                .queryParam("idxNm", "코스피")
                .build()
                .toString();
    }
    
    private List<KospiStockDto.Index> transformDataToDto(String responseData) {
        try {
            JSONArray item = getJsonArray(responseData);
            return filterRecentData(item);
        } catch (Exception requestOpenApiError) {
            log.error("Error during Open API request", requestOpenApiError);
        }
        
        return null;
    }
    
    private JSONArray getJsonArray(String responseData) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(responseData);
        JSONObject response = (JSONObject) jsonObject.get("response");
        JSONObject body = (JSONObject) response.get("body");
        JSONObject items = (JSONObject) body.get("items");
        
        return (JSONArray) items.get("item");
    }
    
    private List<KospiStockDto.Index> filterRecentData(JSONArray items) {
        List<KospiStockDto.Index> kospiStockDtos = new CopyOnWriteArrayList<>();
        String latestDate = getLatestDate(items);
        
        for (Object item : items) {
            JSONObject jsonObject = (JSONObject) item;
            
            if (jsonObject.get("basDt").equals(latestDate)) {
                kospiStockDtos.add(createKospiStockIndexFromJson(jsonObject));
            }
        }
        
        return kospiStockDtos;
    }
    
    private String getLatestDate(JSONArray items) {
        JSONObject firstItem = (JSONObject) items.get(0);
        return (String) firstItem.get("basDt");
    }
    
    private KospiStockDto.Index createKospiStockIndexFromJson(JSONObject jsonObject) {
        return KospiStockDto.Index.builder()
                .basDt((String) jsonObject.get("basDt"))
                .idxNm((String) jsonObject.get("idxNm"))
                .idxCsf((String) jsonObject.get("idxCsf"))
                .epyItmsCnt((String) jsonObject.get("epyItmsCnt"))
                .clpr((String) jsonObject.get("clpr"))
                .vs((String) jsonObject.get("vs"))
                .fltRt((String) jsonObject.get("fltRt"))
                .mkp((String) jsonObject.get("mkp"))
                .hipr((String) jsonObject.get("hipr"))
                .lopr((String) jsonObject.get("lopr"))
                .trqu((String) jsonObject.get("trqu"))
                .trPrc((String) jsonObject.get("trPrc"))
                .lstgMrktTotAmt((String) jsonObject.get("lstgMrktTotAmt"))
                .lsYrEdVsFltRg((String) jsonObject.get("lsYrEdVsFltRg"))
                .lsYrEdVsFltRt((String) jsonObject.get("lsYrEdVsFltRt"))
                .yrWRcrdHgst((String) jsonObject.get("yrWRcrdHgst"))
                .yrWRcrdHgstDt((String) jsonObject.get("yrWRcrdHgstDt"))
                .yrWRcrdLwst((String) jsonObject.get("yrWRcrdLwst"))
                .yrWRcrdLwstDt((String) jsonObject.get("yrWRcrdLwstDt"))
                .basPntm((String) jsonObject.get("basPntm"))
                .basIdx((String) jsonObject.get("basIdx"))
                .build();
    }
}
