package mainproject.stocksite.domain.stock.overall.kosdaq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.cache.CacheService;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockIndex;
import mainproject.stocksite.domain.stock.overall.kosdaq.repository.KosdaqStockIndexRepository;
import mainproject.stocksite.domain.stock.overall.util.DateUtils;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
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

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.service
 * FileName: KosdaqStockIndexUpdater
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KosdaqStockIndexUpdater {
    private static final String CRON_EXPRESSION = "0 0 16 * * *";
    private static final String TIME_ZONE = "Asia/Seoul";
    private static final String KOSDAQ_STOCK_INDICES_CACHE_KEY = "KOSDAQ Stock Indices: ";
    private static final String KOSDAQ_STOCK_INDEX_API_URL = "http://apis.data.go.kr/1160100/service/GetMarketIndexInfoService/getStockMarketIndex";
    private static final int NUM_OF_ROWS = 5;
    private static final int PAGE_NO = 1;
    
    private final KosdaqStockIndexRepository kosdaqStockIndexRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final CacheService cacheService;
    private final RestTemplate restTemplate;
    private final OpenApiSecretInfo openApiSecretInfo;
    
    @PostConstruct
    @Scheduled(cron = CRON_EXPRESSION, zone = TIME_ZONE)
    public void updateKosdaqStockIndices() {
        deleteKosdaqStockIndices();
        cacheService.deleteCacheByKey(KOSDAQ_STOCK_INDICES_CACHE_KEY);
        
        String responseData = requestToOpenApiServer();
        processResponseData(responseData);
    }
    
    private void deleteKosdaqStockIndices() {
        kosdaqStockIndexRepository.deleteAll();
    }
    
    private String requestToOpenApiServer() {
        String requestUrl = buildApiUrl();
        return restTemplate.getForEntity(requestUrl, String.class).getBody();
    }
    
    private String buildApiUrl() {
        return UriComponentsBuilder.fromHttpUrl(KOSDAQ_STOCK_INDEX_API_URL)
                .queryParam("serviceKey", openApiSecretInfo.getServiceKey())
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", PAGE_NO)
                .queryParam("resultType", "json")
                .queryParam("beginBasDt", DateUtils.getFiveDaysAgoToNow())
                .queryParam("idxNm", "코스닥")
                .build()
                .toString();
    }
    
    private void processResponseData(String responseData) {
        try {
            JSONArray item = getJsonArray(responseData);
            saveKosdaqStockIndices(item);
        } catch (Exception requestOpenApiError) {
            log.error("Error during Open API request", requestOpenApiError);
        }
    }
    
    private JSONArray getJsonArray(String responseData) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(responseData);
        JSONObject response = (JSONObject) jsonObject.get("response");
        JSONObject body = (JSONObject) response.get("body");
        JSONObject items = (JSONObject) body.get("items");
        return (JSONArray) items.get("item");
    }
    
    private void saveKosdaqStockIndices(JSONArray item) {
        for (long i = 0; i < item.size(); i++) {
            JSONObject jsonObject = (JSONObject) item.get((int) i);
            KosdaqStockIndex kosdaqStockIndex = createKosdaqStockIndexFromJson(jsonObject, i + 1);
            kosdaqStockIndexRepository.save(kosdaqStockIndex);
        }
    }
    
    private KosdaqStockIndex createKosdaqStockIndexFromJson(JSONObject jsonObject, long id) {
        return KosdaqStockIndex.builder()
                .id(id)
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
