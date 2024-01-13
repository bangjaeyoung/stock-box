package mainproject.stocksite.domain.stock.overall.kosdaq.service;

import lombok.RequiredArgsConstructor;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockIndex;
import mainproject.stocksite.domain.stock.overall.kosdaq.repository.KosdaqStockIndexRepository;
import mainproject.stocksite.domain.stock.overall.util.DateUtils;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.service
 * FileName: KosdaqStockIndexUpdater
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
@Service
@Transactional
@RequiredArgsConstructor
public class KosdaqStockIndexUpdater {
    private static final String KOSDAQ_STOCK_INDEX_API_URL = "http://apis.data.go.kr/1160100/service/GetMarketIndexInfoService/getStockMarketIndex";
    
    private final RestTemplate restTemplate;
    private final OpenApiSecretInfo openApiSecretInfo;
    private final KosdaqStockIndexRepository kosdaqStockIndexRepository;
    
    // 매일 오전 11시 5분 15초에 KOSDAQ 주가지수시세 데이터 저장
    @PostConstruct
    @Scheduled(cron = "15 5 11 * * *", zone = "Asia/Seoul")
    public void updateKosdaqStockIndices() {
        String requestUrl = buildApiUrl();
        ResponseEntity<String> responseData = restTemplate.getForEntity(requestUrl, String.class);
        processResponseData(responseData.getBody());
    }
    
    // 매일 오전 11시 5분에 KOSDAQ 주가지수시세 데이터 삭제
    @Scheduled(cron = "0 5 11 * * *", zone = "Asia/Seoul")
    public void deleteKosdaqStockIndices() {
        kosdaqStockIndexRepository.deleteAll();
    }
    
    private String buildApiUrl() {
        return UriComponentsBuilder.fromHttpUrl(KOSDAQ_STOCK_INDEX_API_URL)
                .queryParam("serviceKey", openApiSecretInfo.getServiceKey())
                .queryParam("numOfRows", 5)
                .queryParam("pageNo", 1)
                .queryParam("resultType", "json")
                .queryParam("beginBasDt", DateUtils.getFiveDaysAgoToNow())
                .queryParam("idxNm", "코스닥")
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
            
            saveKosdaqStockIndices(item);
        } catch (Exception requestOpenApiError) {
            requestOpenApiError.printStackTrace();
        }
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
