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

## 4. 전체적인 흐름
<img src="https://github.com/bangjaeyoung/gyul-box/assets/80241053/80f112f3-9ed0-44c2-943b-8c1a319b6552">

</br>
</br>
</br>

## 5. 맡았던 핵심 기능
### 5.1. 누리집 Open API를 활용한 주식 데이터 저장 및 관리
기존 프론트 서버에서 누리집 Open API를 호출하였음. CORS에러가 발생하지 않았기 때문.   
그러나 성능 상의 이슈(밑에 핵심트러블 링크걸기) 때문에, 백엔드 서버에서 호출하기로 결정.   
매일 주기적으로 호출하여 데이터를 받아오고, DB에 저장.
저장된 데이터는 주기적으로 호출되기 전에 삭제된 후 저장되도록 구현.   

<details>
<summary>상세 설명</summary>
<div markdown="1">
</div>
</details>

</br>

### 5.2. 프록시 형태의 한국투자증권, 네이버 디밸로퍼 Open API를 호출
기존 프론트 서버에서 프록시 서버를 통해 한국투자증권, 네이버 디벨로퍼 open api를 호출했었음.   
이 과정을 백엔드 서버에서 위임하여 처리하도록 구성함.   
프론트엔드는 백엔드 서버에 필요한 정보만 요청하면 백엔드 서버는 프록시 서버의 형태로 open api를 호출하고 데이터를 가공하여 응답내려주는 방식으로 재구현함.

<details>
<summary>상세 설명</summary>
<div markdown="1">
</div>
</details>

</br>

### 5.3. 주식 종목 북마크 기능 구현

<details>
<summary>상세 설명</summary>
<div markdown="1">
</div>
</details>

</br>

## 6. 핵심 트러블 슈팅

원래 누리집 api는 프론트 측에서 요청을 했음.   
CORS 에러가 나지 않았기 때문. (예상 : 누리집은 아무나 쓸 수 있도록 하기 위해 CORS 설정을 풀어준 것 같음)   
그러나, 백엔드 서버가 프록시 서버의 역할로서 누리집 api를 호출하도록 변경   
api 비밀키나 보안적인 요소를 프론트 서버에 노출시키지 않고, 비즈니스적인 요소만 신경쓸 수 있도록 하기 위함.   
가장 큰 이유는 프론트엔드에서 직접 누리집 api를 호출하는 응답 시간이 너무 느렸다. 약 1~3초 정도까지도 걸림.   
백엔드 서버에서 주기적으로 호출하고, db에 저장하여 관리하는 방식으로 약 0.3초 정도로 api 요청 시간을 단축시킴.   

나머지 한국투자증권, 네이버 디밸로퍼 api는 백엔드 서버를 거쳐 요청해올 수 있도록 프록시 개념의 서버로서 벡엔드 서버를 사용   

문제해결에 초점   
누리집 -> 느린응답속도 -> 스케쥴링기능을 통해 DB에 1일단위로 정보저장 -> 성능개선   
한투, 네이버 -> cors에러 발생 -> cors에러를 우회하기 위해 백엔드에서 프록시기능 구현   

</br>

## 7. 그 외 트러블 슈팅
- 증권사 api 요청 시, yml 파일의 서비스 키를 못 받아오는 상황 / 블로깅 했음
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
