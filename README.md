# 💸 Stock Box
><b>국내 실시간 주식 조회 및 모의투자 서비스</b>
>
>주식에 입문하는 사용자들을 위해 구상한 서비스입니다.   
>여러 국내 주식을 찾아보고, 모의투자를 통해 주식매매에 대한 감을 키울 수 있습니다.

</br>

## 1. 실제 서비스 화면

### ✔ 주식 전체 & 종목 TOP10 페이지
<img src="https://github.com/bangjaeyoung/stock-box/assets/80241053/79b663d7-98a9-4cc2-956f-deded5041625"/>

### ✔ 특정 종목 검색
<img src="https://github.com/bangjaeyoung/stock-box/assets/80241053/da88b7fd-089a-484a-b072-a4010681d81f"/>

### ✔ 주식 상세 페이지
<img src="https://github.com/bangjaeyoung/stock-box/assets/80241053/709efb1e-f444-49d8-b879-1d1e8ead36ab"/>

### ✔ 나의 관심 종목 페이지(북마크)
<img src="https://github.com/bangjaeyoung/stock-box/assets/80241053/3c4287c2-e621-4c76-bbe1-8bc6ec9373cb"/>

### ✔ 나의 자산 관리 페이지
<img src="https://github.com/bangjaeyoung/stock-box/assets/80241053/16be105d-7168-4ef2-88b9-ab9f2bb63af6"/>

</br>
</br>
</br>

## 2. 제작 기간 & 참여 인원
- 2023년 1월 3일 ~ 2023년 2월 3일
- 팀 프로젝트(프론트엔드 3명, 백엔드 3명)

</br>

## 3. 사용 기술
#### `Back-end`
  - Java 11
  - Spring Boot 2.7.7
  - Gradle
  - Spring Data JPA
  - H2
  - MySQL 8.0.31

</br>

## 4. ERD 설계
<img src="https://github.com/bangjaeyoung/gyul-box/assets/80241053/929dcc70-8ae0-441d-a554-996cde977dd0" width= 1200 height=600>

</br>
</br>

이미지를 클릭하시면 확대해 볼 수 있습니다.

</br>

## 5. 사용자 요구사항 정의서
<img src="https://github.com/bangjaeyoung/gyul-box/assets/80241053/1404474a-426a-4f3e-88cc-c15727a9edf8">

</br>
</br>

이미지를 클릭하시면 확대해 볼 수 있습니다.

</br>

## 6. 전체적인 흐름
<img src="https://github.com/bangjaeyoung/gyul-box/assets/80241053/80f112f3-9ed0-44c2-943b-8c1a319b6552">

</br>
</br>
</br>

## 7. 맡았던 핵심 기능
### 프록시 서버 형태의 한국투자증권, 네이버 디벨로퍼 Open API 호출

<details>
<summary>상세 설명</summary>
<div markdown="1">

한국투자증권 API는 상세 주식 정보 데이터를, 네이버 디벨로퍼 API는 증권 관련 뉴스 데이터를 위해 사용됩니다.

기존에는 프론트 서버에서 자체 프록시 서버를 통해 외부 API를 호출했습니다.   
이후에는 백엔드 서버에서 외부 API를 호출하여 받은 데이터를 프론트로 전달해주는 방식으로 변경했습니다.   

변경된 호출 흐름은 다음과 같습니다.   
1. 프론트 측에서 필요한 데이터를 백엔드 서버로 요청
2. 백엔드 서버에서 Open API의 서버로 요청한 후, 데이터를 받아옴
3. 받아온 데이터를 프론트 측에 전달

이 과정 속에서 백엔드 서버는 단순 중계 서버 형태의 프록시 서버 역할을 합니다.   
(위의 [전체적인 흐름](#4-전체적인-흐름) 그림을 보면 흐름을 파악하시는 데 도움이 됩니다.)

외부 API를 호출하기 위한 Service Key 노출 등과 같은 보안 위협을 방지하고, 프론트엔드는 사용자 경험과 UI에 더욱 집중할 수 있게 되었습니다.

📌 [관련 Controller 코드](https://github.com/bangjaeyoung/stock-box/blob/main/server/src/main/java/mainproject/stocksite/domain/stock/detail/controller/DetailedStockController.java)   
📌 [관련 Service 코드](https://github.com/bangjaeyoung/stock-box/blob/main/server/src/main/java/mainproject/stocksite/domain/stock/detail/service/DetailedStockService.java)

</div>
</details>

### 누리집 Open API를 활용한 주식 데이터 저장 및 관리

<details>
<summary>상세 설명</summary>
<div markdown="1">

Spring RestTemplate를 사용하여 누리집 Open API를 호출했습니다.   

누리집 Open API는 전체 주식 종목 리스트를 조회하기 위해 사용됩니다.   
데이터들은 매일 오전 11시에 업데이트되기 때문에, Spring Scheduler를 통해 주기적으로 호출하여 데이터를 받아오도록 구현했습니다.

```Java
@PostConstruct
@Scheduled(cron = "15 5 11 * * *", zone = "Asia/Seoul")  // 매일 오전 11시 5분 15초에 주식시세정보 데이터 불러옴
public void getAndSaveKOSDAQStockIndex() {

    String url = STOCK_DEFAULT_URL + "/getStockMarketIndex";

    ...
```

📌 [원본 코드](https://github.com/bangjaeyoung/stock-box/blob/main/server/src/main/java/mainproject/stocksite/domain/stock/overall/save/SaveKOSDAQStockIndex.java)

매일 받아오는 데이터의 수는 상당히 많기 때문에, 불러오기 이전에 DB에 저장된 데이터를 삭제할 수 있도록 했습니다.

```Java
// 매일 오전 11시 5분에 DB에 있는 주식시세정보 데이터 삭제
@Scheduled(cron = "0 5 11 * * *", zone = "Asia/Seoul")
public void deleteKOSPIStockList() {
    kosdaqStockIndexRepository.deleteAll();
}
```

코스닥, 코스피별로 지수정보, 시세정보를 불러오기 위한 총 4개의 비즈니스 로직을 작성했습니다.   
누리집 Open API 호출과 관련된 로직들은 모두 해당 [Save 폴더](https://github.com/bangjaeyoung/stock-box/tree/main/server/src/main/java/mainproject/stocksite/domain/stock/overall/save)에 있습니다.   

</div>
</details>

### 모의 투자 기능 구현

<details>
<summary>상세 설명</summary>
<div markdown="1">

초기 유저는 모의투자를 위한 기본금 1000만원이 주어집니다.  

특정 주식의 사용자 UI에서 매수 버튼을 누르면 BUY / 매도 버튼을 누르면 SELL의 타입으로 거래를 생성합니다.   
📌 [매수, 매도 관련 Controller 코드](https://github.com/bangjaeyoung/stock-box/blob/22428406b17d0aa35494488e57e586f078d12849/server/src/main/java/mainproject/stocksite/domain/trade/controller/TradeController.java#L29C5-L35C6)

해당 주식 종목을 이미 갖고 있는지, 금액은 충분한지 등의 여러 조건들을 거쳐 거래가 처리됩니다.   
📌 [매수, 매도 관련 Service 코드](https://github.com/bangjaeyoung/stock-box/blob/22428406b17d0aa35494488e57e586f078d12849/server/src/main/java/mainproject/stocksite/domain/trade/service/TradeService.java#L26C5-L82C6)

정확한 계산이 필요한 돈 거래이기 때문에, Java에서 숫자를 정밀하게 저장하고 표현할 수 있는 BigDecimal 타입을 사용했습니다.   
Trade라는 별도의 엔티티를 만든 이유는 투자 연습에 알맞게 투자 기록 조회 기능도 구현하기 위함입니다.   

모의 투자 기능은 급하게 맡아서 구현했던 기능이었기에, 불필요한 로직이 많고 가독성 또한 좋지 못합니다.   
추후 회고에서 다룰 부분이기도 합니다.   

</div>
</details>

### 주식 종목 북마크 CRUD 기능 구현

<details>
<summary>상세 설명</summary>
<div markdown="1">

📌 [북마크 기능 관련 폴더](https://github.com/bangjaeyoung/stock-box/tree/main/server/src/main/java/mainproject/stocksite/domain/bookmark)

</div>
</details>

</br>

## 8. 핵심 트러블 슈팅

### 1) 문제 상황
기존에는 프론트측에서 누리집 API를 호출했습니다.   
프론트측에서 호출이 가능했던 이유는 CORS 에러가 발생하지 않았기 때문입니다.   
(누리집은 공공 데이터 포털이기 때문에, 모든 CORS 설정을 허용한거라고 추측하고 있습니다.)   

이러한 방식은 데이터 응답 속도가 느리다는 단점이 있었습니다.

### 2) 문제 해결 과정
백엔드 서버에서 누리집 API를 호출하는 것으로 개선했습니다. 과정은 다음과 같습니다.
1. Spring RestTemplate을 사용하여 누리집 API를 호출하여 데이터를 받아옴   
2. 받아온 데이터를 DB에 저장   
3. 위 1, 2번 과정을 매일 반복하여 DB의 데이터를 지속적으로 업데이트   
4. 프론트 측에서 필요한 데이터를 백엔드 서버로 요청하면, 백엔드 서버는 DB로 요청하여 데이터를 받아와 전달

위의 [전체적인 흐름](#4-전체적인-흐름) 사진을 참고하시면 이해하는 데 도움이 됩니다.

### 3) 개선된 점
1. 프론트와 백엔드의 역할 분리     
2. 보안적인 요소 노출 제거   
3. 기존 방식의 데이터를 받아오던 시간을 약 1~2초에서 약 0.3초로 1/3 정도의 요청 시간 단축

나머지 한국투자증권, 네이버 디벨로퍼 API는 CORS 에러로 인해, 애초에 백엔드 서버를 거쳐 요청해올 수 있도록 구현했습니다.

</br>

## 9. 그 외 트러블 슈팅
<details>
<summary><b>yml 파일의 환경 변수를 불러오지 못하고 null 값이 되는 문제</b></summary>
<div markdown="1">

Service Key의 정보만 담긴 클래스를 따로 만들고, 각 Service Layer에서 의존성을 주입받아 사용하여 해결했습니다.   

```Java
@Getter
@Configuration
@ConfigurationProperties(prefix = "open-api")
public class OpenApiSecretInfo {

    // 한국투자증권 API 요청 관련 키
    private String appKey;
    private String appSecret;

    // 누리집 API 요청 관련 키
    private String serviceKey;

    // 네이버 검색 API 요청 관련 키
    private String naverClientId;
    private String naverClientSecret;
}
```

해당 문제를 [블로깅](https://jaeyoungb.tistory.com/268)하여 확실히 정리해둘 수 있었습니다.

</div>
</details>

<details>
<summary><b>외부 서버의 초당 요청량 제한</b></summary>
<div markdown="1">

외부 서버로의 초당 요청량에 대한 제한이 있었습니다.

초기에는 외부 서버로부터 응답되는 요청량 초과에 대한 예외를 잡아서, 재요청하는 로직을 구성했습니다. (try-catch문)   
그 결과, 비즈니스 로직이 다소 복잡해지고 새로운 요청과 재요청이 만나 계속해서 에러가 발생하는 문제가 있었습니다.

결국, 요청량 제한에 대한 에러 메시지가 백엔드 서버로 도착하면 커스텀한 에러 메시지를 프론트에 내려주기로 했습니다.   

프론트 측에서는 해당 에러 메시지를 받으면,   
백엔드 서버로 다시 요청을 보내거나 캐싱과 로드 밸런싱 기능을 활용해서 해결하도록 개선했습니다.   

실무에서는 외부 서버와 따로 계약을 맺고 요청량 제한 문제가 발생하지 않도록 해결할 것 같습니다.   

</div>
</details>

<details>
<summary><b>누리집 데이터가 매일 갱신되지 않는 문제</b></summary>
<div markdown="1">

백엔드 서버에서 매일 주기적으로 누리집 API를 호출하여 데이터를 받아오지만, 누리집 데이터가 매일 업데이트되진 않았습니다.   

결국 오늘 날짜로부터 5일 전까지의 데이터를 받아오도록 하고,   
프론트 측으로 그 5일간의 데이터 중 가장 최신의 데이터를 필터링하여 전달하도록 개선하였습니다.   

```Java
// 외부 API 호출
UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("beginBasDt", dateConfig.getFromFiveDaysAgoToNow()) // 해당 부분
                .build();

// Service 로직
public List<KOSPIStockIndex> getKOSPIStockIndex() {
    List<KOSPIStockIndex> foundIndices = kospiStockIndexRepository.findAll();
    verifyExistsData(foundIndices);

    // 해당 부분
    String theMostRecentBasDt = foundIndices.get(0).getBasDt();
    List<KOSPIStockIndex> theMostRecentStockIndices = foundIndices.stream().filter(e -> e.getBasDt().equals(theMostRecentBasDt)).collect(Collectors.toList());

    return theMostRecentStockIndices;
}
```
:pushpin: [외부 API 호출 코드](https://github.com/bangjaeyoung/stock-box/blob/ecf73055a22d0abadb064f81f303bda6879f860e/server/src/main/java/mainproject/stocksite/domain/stock/overall/save/SaveKOSDAQStockIndex.java#L40C9-L47C26)   
:pushpin: [비즈니스 로직 코드](https://github.com/bangjaeyoung/stock-box/blob/ecf73055a22d0abadb064f81f303bda6879f860e/server/src/main/java/mainproject/stocksite/domain/stock/overall/service/OverallStockService.java#L31C5-L39C6)

</div>
</details>

<details>
<summary><b>오늘로부터 5일전 날짜를 구하는 로직에서의 문제</b></summary>
<div markdown="1">

오늘로부터 5일전 날짜는 String 타입의 yyyyMMdd 형태의 값이 필요했습니다. ex.20230830   
문제가 있던 기존 코드는 다음과 같았습니다.

```Java
@Getter
@Component
public class DateConfig {
    LocalDate now = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String formattedNow = now.format(formatter);

    String fromFiveDaysAgoToNow = String.valueOf(Integer.parseInt(formattedNow) - 5);
}
```

20230804인 날짜로부터 5일전일 경우, 위 코드로는 20230799가 되어서 문제가 발생했었습니다.   

개선한 코드는 다음과 같습니다.
```Java
@Getter
@Component
public class DateConfig {
    public String getFromFiveDaysAgoToNow() {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMdd");

        cal.add(Calendar.DATE, -5);

        String fromFiveDaysAgoToNow = sdformat.format(cal.getTime());

        return fromFiveDaysAgoToNow;
    }
}
```
</div>
</details>

</br>

## 10. 아쉬운 점 및 회고

<details>
<summary><b>외부 Open API 데이터를 그대로 프론트측에 전달</b></summary>
<div markdown="1">

외부 Open API는 이번 프로젝트에서 처음 다루었습니다.   
RestTemplate을 활용하여 API를 호출하고 받아온 데이터와 DB에 저장한 엔티티 필드 간 매핑 작업은 저에겐 쉬운 작업이 아니었습니다.   

결국 시간을 많이 소요하게 되었고, DB에 저장된 데이터 그대로를 그대로 프론트로 응답해주었습니다.   

외부 Open API에서 받아온 데이터 중 필요한 데이터만을 추려서 DB에 저장하고,   
이해하기 쉬운 필드명으로 응답 dto를 구성했다면 더 좋았겠다는 아쉬움이 남습니다.   
  
</div>
</details>

<details>
<summary><b>Spring RestTemplate vs WebClient</b></summary>
<div markdown="1">

다음에 외부 서버의 API를 호출해야 한다면, 스프링 5.0부터 도입된 WebClient 인터페이스를 사용하는 것도 괜찮을 것 같다는 생각입니다.

물론 WebFlux와 같은 쉽지 않은 개념을 알아야 하고 단순히 API 호출을 위해 WebFlux 라이브러리를 추가해야 하는 것은 팀원들과 상의해봐야할 일이지만,   
상황이 허락해준다면 유지 관리 모드(deprecated)인 RestTemplate을 대체해서 한 번 사용해보고 싶습니다.   
  
</div>
</details>

<details>
<summary><b>Spring Batch, Quartz 사용</b></summary>
<div markdown="1">

받아오는 주식 데이터량은 결코 작지 않았습니다.

대용량 레코드 처리에 유용한 Spring Batch와 전용 스케쥴러인 Quartz 스케쥴러를 함께 적용해봤으면 좋지 않았을까하는 아쉬움이 남습니다.   
추후 기회가 생긴다면 해당 기술들을 학습하여 적용해보고 싶습니다.

</div>
</details>

<details>
<summary><b>모의 투자 기능 로직 고도화 필요성</b></summary>
<div markdown="1">

모의 투자 기능은 원래 다른 팀원에게 할당되어 있었고, 프로젝트 막바지에 제가 전달받아 급하게 구현했었습니다.   

마감기한이 얼마남지 않은 상황에서 구현한 해당 기능은 많이 부실하다고 생각합니다.   
보안적인 측면을 좀 더 강화시키고 가독성과 유지보수성이 좋은 코드로 리팩토링이 필요할 것 같습니다.

</div>
</details>

<details>
<summary><b>DB에 주식 데이터가 존재하는지 확인하는 메서드</b></summary>
<div markdown="1">

현재는 데이터를 `findAll()` 메서드를 통해 모두 받아와서 `isEmpty()` 메서드를 통해 존재 유무를 체크하고 있습니다.   

주식 데이터의 존재 유무를 체크하는 exists 메서드를 Querydsl을 통한 JPQL 쿼리로 작성했으면 좋을 것 같습니다.   
:pushpin: [참고한 블로그](https://jojoldu.tistory.com/516)   

</div>
</details>

<details>
<summary><b>외부 서버에 대한 높은 의존성</b></summary>
<div markdown="1">

이 프로젝트는 외부 Open API의 의존성이 매우 높은 프로젝트입니다.   
주식 관련 서비스이기 때문에, 주식 데이터를 받아오지 못하면 해당 서비스는 아무것도 아니게 됩니다.   

제 생각에는 개인이 이 주제로 프로젝트를 진행하기에는 신경써야할 많은 제약이 있다고 생각합니다.   
이 프로젝트를 진행하면서, 요청량 제한과 같은 문제를 해결할 수 있는 선에서 최대한 극복하려고 했던 것 같습니다.

</div>
</details>
