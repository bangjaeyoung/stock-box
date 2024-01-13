package mainproject.stocksite.domain.stock.overall.kosdaq.service;

import lombok.RequiredArgsConstructor;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockList;
import mainproject.stocksite.domain.stock.overall.kosdaq.repository.KosdaqStockListRepository;
import mainproject.stocksite.domain.stock.overall.util.DateUtils;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.service
 * FileName: KosdaqStockListUpdater
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
@Service
@Transactional
@RequiredArgsConstructor
public class KosdaqStockListUpdater {
    private static final String KOSDAQ_STOCK_LIST_API_URL = "http://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo";
    
    private final RestTemplate restTemplate;
    private final OpenApiSecretInfo openApiSecretInfo;
    private final KosdaqStockListRepository kosdaqStockListRepository;
    
    // 매일 오전 11시 5분 15초에 KOSDAQ 주식시세 데이터 저장
    @PostConstruct
    @Scheduled(cron = "15 5 11 * * *", zone = "Asia/Seoul")
    public void updateKosdaqStockLists() {
        String requestUrl = buildApiUrl();
        ResponseEntity<String> responseData = restTemplate.getForEntity(requestUrl, String.class);
        processResponseData(responseData.getBody());
    }
    
    // 매일 오전 11시 5분에 KOSDAQ 주식시세 데이터 삭제
    @Scheduled(cron = "0 5 11 * * *", zone = "Asia/Seoul")
    public void deleteKosdaqStockLists() {
        kosdaqStockListRepository.deleteAll();
    }
    
    private String buildApiUrl() {
        return UriComponentsBuilder.fromHttpUrl(KOSDAQ_STOCK_LIST_API_URL)
                .queryParam("serviceKey", openApiSecretInfo.getServiceKey())
                .queryParam("numOfRows", 2000)
                .queryParam("pageNo", 1)
                .queryParam("resultType", "json")
                .queryParam("beginBasDt", DateUtils.getFiveDaysAgoToNow())
                .queryParam("mrktCls", "KOSDAQ")
                .build()
                .toString();
    }
    
    private void processResponseData(String responseDataBody) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(responseDataBody);
            JSONObject response = (JSONObject) jsonObject.get("response");
            JSONObject body = (JSONObject) response.get("body");
            JSONObject items = (JSONObject) body.get("items");
            JSONArray item = (JSONArray) items.get("item");
            
            saveKosdaqStockLists(item);
        } catch (Exception requestOpenApiError) {
            requestOpenApiError.printStackTrace();
        }
    }
    
    private void saveKosdaqStockLists(JSONArray item) {
        for (long i = 0; i < item.size(); i++) {
            JSONObject jsonObject = (JSONObject) item.get((int) i);
            KosdaqStockList kosdaqStockList = createKosdaqStockListFromJson(jsonObject, i + 1);
            
            kosdaqStockListRepository.save(kosdaqStockList);
        }
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
}
