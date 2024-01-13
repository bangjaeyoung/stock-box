package mainproject.stocksite.domain.stock.overall.save;

import lombok.RequiredArgsConstructor;
import mainproject.stocksite.domain.stock.overall.config.DateConfig;
import mainproject.stocksite.domain.stock.overall.entity.KospiStockList;
import mainproject.stocksite.domain.stock.overall.repository.KospiStockListRepository;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class SaveKospiStockList {
    private static final String STOCK_DEFAULT_URL = "http://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService";
    
    private final OpenApiSecretInfo openApiSecretInfo;
    private final KospiStockListRepository kospiStockListRepository;
    private final RestTemplate restTemplate;
    
    @PostConstruct
    @Scheduled(cron = "15 5 11 * * *", zone = "Asia/Seoul")  // 매일 오전 11시 5분 15초에 주식시세정보 데이터 불러옴
    public void getAndSaveKOSPIStockList() {
        
        String url = STOCK_DEFAULT_URL + "/getStockPriceInfo";
        
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("serviceKey", openApiSecretInfo.getServiceKey())
                .queryParam("numOfRows", 1000)
                .queryParam("pageNo", 1)
                .queryParam("resultType", "json")
                .queryParam("beginBasDt", DateConfig.getFiveDaysAgoToNow())
                .queryParam("mrktCls", "KOSPI")
                .build();
        
        ResponseEntity<String> responseData = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);
        
        String responseDataBody = responseData.getBody();
        
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(responseDataBody);
            JSONObject response = (JSONObject) jsonObject.get("response");
            
            JSONObject body = (JSONObject) response.get("body");
            
            JSONObject items = (JSONObject) body.get("items");
            
            JSONArray item = (JSONArray) items.get("item");
            
            for (long i = 0; i < item.size(); i++) {
                JSONObject tmp = (JSONObject) item.get((int) i);
                KospiStockList kospiStockList = KospiStockList.builder()
                        .id(i + 1)
                        .basDt((String) tmp.get("basDt"))
                        .srtnCd((String) tmp.get("srtnCd"))
                        .isinCd((String) tmp.get("isinCd"))
                        .itmsNm((String) tmp.get("itmsNm"))
                        .mrktCtg((String) tmp.get("mrktCtg"))
                        .clpr((String) tmp.get("clpr"))
                        .vs((String) tmp.get("vs"))
                        .fltRt((String) tmp.get("fltRt"))
                        .mkp((String) tmp.get("mkp"))
                        .hipr((String) tmp.get("hipr"))
                        .lopr((String) tmp.get("lopr"))
                        .trqu((String) tmp.get("trqu"))
                        .trPrc((String) tmp.get("trPrc"))
                        .lstgStCnt((String) tmp.get("lstgStCnt"))
                        .mrktTotAmt((String) tmp.get("mrktTotAmt"))
                        .build();
                kospiStockListRepository.save(kospiStockList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 매일 오전 11시 5분에 DB에 있는 주식시세정보 데이터 삭제
    @Scheduled(cron = "0 5 11 * * *", zone = "Asia/Seoul")
    public void deleteKOSPIStockList() {
        kospiStockListRepository.deleteAll();
    }
}
