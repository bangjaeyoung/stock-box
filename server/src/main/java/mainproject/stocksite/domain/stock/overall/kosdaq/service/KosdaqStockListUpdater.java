package mainproject.stocksite.domain.stock.overall.kosdaq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.cache.CacheService;
import mainproject.stocksite.domain.stock.overall.kosdaq.dto.KosdaqStockDto;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockList;
import mainproject.stocksite.domain.stock.overall.kosdaq.mapper.KosdaqStockMapper;
import mainproject.stocksite.domain.stock.overall.kosdaq.repository.KosdaqStockListRepository;
import mainproject.stocksite.domain.stock.overall.util.DateUtils;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.List;

import static mainproject.stocksite.domain.stock.overall.cache.CacheService.KOSDAQ_STOCK_LISTS_CACHE_KEY;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.service
 * FileName: KosdaqStockListUpdater
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KosdaqStockListUpdater {
    private static final String CRON_EXPRESSION = "0 0 16 * * *";
    private static final String TIME_ZONE = "Asia/Seoul";
    private static final String KOSDAQ_STOCK_LIST_API_URL = "http://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo";
    private static final int NUM_OF_ROWS = 2000;
    private static final int PAGE_NO = 1;
    
    private final KosdaqStockListRepository kosdaqStockListRepository;
    private final CacheService cacheService;
    private final RestTemplate restTemplate;
    private final OpenApiSecretInfo openApiSecretInfo;
    private final KosdaqStockMapper kosdaqStockMapper;
    
    @PostConstruct
    @Scheduled(cron = CRON_EXPRESSION, zone = TIME_ZONE)
    public void updateKosdaqStockLists() {
        deleteKosdaqStockLists();
        cacheService.deleteCacheByKey(KOSDAQ_STOCK_LISTS_CACHE_KEY);
        
        String responseData = requestToOpenApiServer();
        processResponseData(responseData);
        
        List<KosdaqStockDto.ListResponse> responseDtos = getListResponses();
        cacheService.saveCacheValue(KOSDAQ_STOCK_LISTS_CACHE_KEY, responseDtos);
    }
    
    public void deleteKosdaqStockLists() {
        kosdaqStockListRepository.deleteAll();
    }
    
    private String requestToOpenApiServer() {
        String requestUrl = buildApiUrl();
        return restTemplate.getForEntity(requestUrl, String.class).getBody();
    }
    
    private String buildApiUrl() {
        return UriComponentsBuilder.fromHttpUrl(KOSDAQ_STOCK_LIST_API_URL)
                .queryParam("serviceKey", openApiSecretInfo.getServiceKey())
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", PAGE_NO)
                .queryParam("resultType", "json")
                .queryParam("beginBasDt", DateUtils.getFiveDaysAgoToNow())
                .queryParam("mrktCls", "KOSDAQ")
                .build()
                .toString();
    }
    
    private void processResponseData(String responseData) {
        try {
            JSONArray item = getJsonArray(responseData);
            saveKosdaqStockLists(item);
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
    
    private void saveKosdaqStockLists(JSONArray item) {
        String latestDate = getLatestDate(item);
        for (long i = 0; i < item.size(); i++) {
            JSONObject jsonObject = (JSONObject) item.get((int) i);
            if (jsonObject.get("basDt").equals(latestDate)) {
                KosdaqStockList kosdaqStockList = createKosdaqStockListFromJson(jsonObject, i + 1);
                kosdaqStockListRepository.save(kosdaqStockList);
            }
        }
    }
    
    private String getLatestDate(JSONArray item) {
        JSONObject firstItem = (JSONObject) item.get(0);
        return (String) firstItem.get("basDt");
    }
    
    private KosdaqStockList createKosdaqStockListFromJson(JSONObject jsonObject, long id) {
        return KosdaqStockList.builder()
                .id(id + 1)
                .basDt((String) jsonObject.get("basDt"))
                .srtnCd((String) jsonObject.get("srtnCd"))
                .isinCd((String) jsonObject.get("isinCd"))
                .itmsNm((String) jsonObject.get("itmsNm"))
                .mrktCtg((String) jsonObject.get("mrktCtg"))
                .clpr((String) jsonObject.get("clpr"))
                .vs((String) jsonObject.get("vs"))
                .fltRt((String) jsonObject.get("fltRt"))
                .mkp((String) jsonObject.get("mkp"))
                .hipr((String) jsonObject.get("hipr"))
                .lopr((String) jsonObject.get("lopr"))
                .trqu((String) jsonObject.get("trqu"))
                .trPrc((String) jsonObject.get("trPrc"))
                .lstgStCnt((String) jsonObject.get("lstgStCnt"))
                .mrktTotAmt((String) jsonObject.get("mrktTotAmt"))
                .build();
    }
    
    private List<KosdaqStockDto.ListResponse> getListResponses() {
        List<KosdaqStockList> kosdaqStockLists = kosdaqStockListRepository.findAll();
        return kosdaqStockMapper.kosdaqStockListsToResponseDtos(kosdaqStockLists);
    }
}
