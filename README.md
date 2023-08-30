
# 💸 Stock-Box
><b>국내 실시간 주식 조회 및 모의 투자 서비스</b>
>
>주식에 입문하는 분들을 타겟하여 제작하였습니다.   
>모의 투자를 통해 주식 매매에 대한 감을 키울 수 있는 서비스입니다.

</br>

## 1. 제작 기간 & 참여 인원
- 2023년 1월 3일 ~ 2023년 2월 3일
- 팀 프로젝트(프론트엔드 3명, 백엔드 3명)

</br>

## 2. 사용 기술
#### `Back-end`
  - Java 11
  - Spring Boot 2.7.7
  - Gradle
  - Spring Data JPA
  - H2
  - MySQL 8.0.31

</br>

## 3. ERD 설계
<img src="https://github.com/bangjaeyoung/gyul-box/assets/80241053/929dcc70-8ae0-441d-a554-996cde977dd0" width=800 height=400>

</br>
</br>

이미지를 클릭하시면 확대해 볼 수 있습니다.

</br>

## 4. 사용자 요구사항 정의서
<img src="https://github.com/bangjaeyoung/gyul-box/assets/80241053/1404474a-426a-4f3e-88cc-c15727a9edf8">

</br>
</br>

이미지를 클릭하시면 확대해 볼 수 있습니다.

</br>

## 4. 전체적인 흐름
<img src="https://github.com/bangjaeyoung/gyul-box/assets/80241053/80f112f3-9ed0-44c2-943b-8c1a319b6552">

</br>
</br>
</br>

## 5. 맡았던 핵심 기능
### 5.1. 프록시 서버처럼 한국투자증권, 네이버 디밸로퍼 Open API를 호출

<details>
<summary>상세 설명</summary>
<div markdown="1">

한국투자증권 API는 상세 주식 정보 데이터를, 네이버 디밸로퍼 API는 증권 관련 뉴스 데이터를 위해 사용됩니다.

기존에 프론트 서버에서 자체 프록시 서버를 통해 외부 API를 호출하는 방식을 이용했습니다.   
이후 백엔드 서버에서 외부 API를 호출한 후, 그 데이터를 프론트로 응답해주는 방식으로 변경하였습니다.   

개선된 호출 흐름은 다음과 같습니다.

1. 프론트 측에서 Open API에서 필요한 데이터를 백엔드 서버로 요청합니다.
2. 백엔드 서버에서 Open API의 서버로 요청을 한 후, 데이터를 받아옵니다.
3. 받아온 데이터를 프론트 측에 내려줍니다.

이 과정 속에서 백엔드 서버는 단순 중계 서버 형태의 **프록시 서버 역할**을 합니다.   
(위의 📌 [전체적인 흐름](#4-전체적인-흐름) 그림을 보면 흐름을 파악하시는 데 도움이 됩니다.)

외부 API를 호출하기 위한 Service Key 노출 등과 같은 보안 위협을 방지하고, 프론트엔드는 사용자 경험과 UI에 더욱 집중할 수 있게 되었습니다.

📌 [DetailedStockController.java](https://github.com/bangjaeyoung/stock-box/blob/main/server/src/main/java/mainproject/stocksite/domain/stock/detail/controller/DetailedStockController.java)   
📌 [DetailedStockService.java](https://github.com/bangjaeyoung/stock-box/blob/main/server/src/main/java/mainproject/stocksite/domain/stock/detail/service/DetailedStockService.java)

</div>
</details>

</br>

### 5.2. 누리집 Open API를 활용한 주식 데이터 저장 및 관리

<details>
<summary>상세 설명</summary>
<div markdown="1">

  Spring RestTemplate를 활용하여 누리집 Open API를 호출하였습니다.   

누리집의 데이터는 전체 주식 종목 리스트를 조회하기 위해 필요한 데이터입니다.   
데이터들은 매일 오전 11시에 업데이트되기 때문에, 다음과 같이 Spring Scheduler를 통해 주기적으로 호출해서 데이터를 받아오도록 구현했습니다.

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
해당 [Save 폴더](https://github.com/bangjaeyoung/stock-box/tree/main/server/src/main/java/mainproject/stocksite/domain/stock/overall/save) 안의 클래스들은 모두 위 정보들을 불러오기 위한 누리집 Open API 호출과 관련된 로직들이 존재합니다.

</div>
</details>

</br>

### 5.3. 모의 투자 기능 구현

<details>
<summary>상세 설명</summary>
<div markdown="1">

처음 유저가 가입하면 모의투자 연습을 위한 기본금으로 1000만원이 주어집니다.  

특정 주식의 사용자 UI에서 **매수** 버튼을 누르면 **BUY** / **매도** 버튼을 누르면 **SELL**의 타입으로 거래를 생성합니다.   
📌 [매수, 매도 관련 Controller 코드](https://github.com/bangjaeyoung/stock-box/blob/22428406b17d0aa35494488e57e586f078d12849/server/src/main/java/mainproject/stocksite/domain/trade/controller/TradeController.java#L29C5-L35C6)

이미 해당 주식 종목을 갖고 있는지 확인하고, 금액은 충분한지 등의 여러 조건들을 거쳐 거래가 처리됩니다.   
📌 [매수, 매도 관련 Servicea 코드](https://github.com/bangjaeyoung/stock-box/blob/22428406b17d0aa35494488e57e586f078d12849/server/src/main/java/mainproject/stocksite/domain/trade/service/TradeService.java#L26C5-L82C6)

돈 거래이기 때문에, Java에서 숫자를 정밀하게 저장하고 표현할 수 있는 **BigDecimal** 타입을 사용했습니다.   
Trade라는 별도의 엔티티를 만든 이유는 투자 연습에 알맞게 투자 기록 조회 기능도 포함하기 위함입니다.   

모의 투자 기능을 급하게 맡아서 구현했던 상황이었기 때문에, 불필요한 로직이 많고 가독성이 좋지 못합니다.   
추후 회고에서 다룰 부분이기도 합니다.   

</div>
</details>

</br>

### 5.4. 주식 종목 북마크 CRUD 기능 구현
📌 [북마크 관련 폴더](https://github.com/bangjaeyoung/stock-box/tree/main/server/src/main/java/mainproject/stocksite/domain/bookmark)

</br>

## 6. 핵심 트러블 슈팅

### 6.1. 문제 상황
기존에는 프론트에서 누리집 API를 호출했습니다.   
프론트에서 호출이 가능했던 이유는 CORS 에러가 발생하지 않았기 때문입니다.   
(누리집은 공공 데이터 포털이기 때문에, 모든 CORS 설정을 허용한거라고 추측하고 있습니다.)   

이러한 방식은 데이터의 **응답 속도가 느리다**는 단점이 있었습니다.

### 6.2. 문제 해결 과정
필요한 기능들을 모두 개발한 후, 백엔드 서버에서 누리집 API를 호출하는 것으로 개선했습니다.
1. Spring RestTemplate을 사용하여 누리집 API를 호출합니다.
2. 응답된 데이터를 DB에 저장합니다.
3. 위 1, 2번 과정을 주기적으로 반복하여 DB의 데이터를 업데이트합니다.
4. 프론트 측에서 필요한 데이터를 백엔드 서버로 요청하면, 백엔드 서버는 DB로 요청하여 데이터를 가져옵니다.

위의 📌 [전체적인 흐름](#4-전체적인-흐름) 사진과 📌 [누리집 관련 설명](#52-누리집-open-api를-활용한-주식-데이터-저장-및-관리)을 보면 이해하시는 데 도움이 됩니다.

### 6.3. 개선된 점
1. 프론트와 백엔드의 역할 분리
2. 보안적인 요소 노출 제거
3. 데이터를 받아오는 데 걸리는 시간을 **약 1~2초에서 약 0.3초로 1/3 정도의 요청 시간 단축**


나머지 한국투자증권, 네이버 디밸로퍼 API는 애초에 CORS 에러가 발생했기 때문에, 백엔드 서버를 거쳐 요청해올 수 있도록 구현했습니다.

</br>

## 7. 그 외 트러블 슈팅
- <details>
<summary>외부 Open API 호출 시, yml 파일의 Service Key를 받아오지 못하고 null 값이 되는 문제</summary>
<div markdown="1">
  
  해당 과정을 [블로깅](https://jaeyoungb.tistory.com/268)을 통해 정리했습니다.

  최종적으로 개선된 코드는 다음과 같이 Service Key의 정보만 담긴 클래스를 따로 만들고, 각 Service Layer에서 의존성을 주입받아 사용하도록 개선했습니다.\
  
</div>
</details>

- 외부 open api 요청량 제한 문제 / 아쉬운 점, 해결했던 점
- 누리집 데이터가 매일 오전 11시에 갱신된다고 가정. But, 업데이트가 매번 되진 않음 -> 해결 : 현재일로 5일전의 날짜를 담아서 호출
- DateConfig 클래스 트러블 슈팅 문제 / 원인 결과 해결

</br>

## 8. 아쉬운 점 및 회고
- api 호출 및 응답 데이터에 대한 커스텀 필드 사용 안함
- Spring Batch, Quartz 사용 / 메모장 참고
- 급한 기능 추가로 인한 비효율적인 ERD 설계
- 모의 투자 기능 로직 고도화 필요성
- db에 주식 데이터가 있는지에 대한 검증 메서드(exist 메서드) / 아쉬운 점 : querydsl의 count 메서드 사용 / jojoldu님 블로그 참고
