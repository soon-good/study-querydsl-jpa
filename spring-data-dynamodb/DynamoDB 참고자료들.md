# DynamoDB 참고자료들



## Amazon DynamoDB 공식 자료

- [Amazon DynamoDB Documentation 메인 페이지](https://docs.aws.amazon.com/dynamodb/index.html)
  - Developer Guide, API Reference 페이지 네비게이션 페이지
- [Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)<br>
- [API PAGE](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)

<br>

세팅 및 개발 하면서 실제로 참고했던 자료들 (퀵 내비)<br>

- docker 환경설정 관련 내용들
  - [Setting Up DynamoDB (Web Service)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SettingUp.DynamoWebService.html)
  - [Setting Up DynamoDB Local (Downloadable Version)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)
  - [Deploying DynamoDB Locally on Your Computer - Amazon DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)

- [DynamoDB 의 `Table`, `Item`, `Attribute` ](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.CoreComponents.html)
  - 세컨더리 인덱스, DynamoDB Streams 까지 읽어봐야 하는데, 아직 모두 읽어보지는 못했다.
- [Use Global Secondary Indexes in DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GSI.html)

- [DynamoDB Annotation들](https://docs.aws.amazon.com/ko_kr/amazondynamodb/latest/developerguide/DynamoDBMapper.Annotations.html) 
- 



## Dynabase

dynamodb 를 매우 자주 사용한다면, 한번 구매를 고려해봐도 좋겠군 하고 생각만 해봤다.<br>

[Dynobase - Professional DynamoDB GUI Client](https://dynobase.dev/#pricing) 

<br>

## 로컬 Docker 기반 환경 세팅시 참고한 자료들

<br>

## spring-data-dynamodb 연동시 참고한 자료들 

한국 자료들은 훑어보기만 하려다가 어떤 자료를 보다가 꼬여서 에러가 왜 난거지? 하다가, 하루 정도를 소모했다. 그런데, 해외 자료를 보고 공식문서를 보면서 하다보니 1시간만에 해결됐다.<br>

<br>

해외자료

- [Querying DynamoDB by Date Range](https://medium.com/cloud-native-the-gathering/querying-dynamodb-by-date-range-899b751a6ef2)

- [DynamoDB in a SpringBoot Application Using Spring Data](https://www.baeldung.com/spring-data-dynamodb)
  - 다소 옛날 방식의 예제.
  - 그래도 전체 과정을 훑어볼수 있기에 한번 읽어봤다.
- [Spring Boot CRUD Example using AWS DynamoDB]([Spring Boot CRUD Example using AWS DynamoDB - YouTube](https://www.youtube.com/watch?v=3ay92ZdCgwQ))



한국자료들

- [다이나모디비 특징](https://m.blog.naver.com/hys1753/221795921828)
- [다이나모디비의 파티션키와 정렬키](https://pearlluck.tistory.com/528)

- [AWS Dynamodb 와 Spring을 이용한 CRUD API](https://eun-dolphin.tistory.com/25)
- [우아한형제들 | Spring Boot 에서 Repository 로 DynamoDB 조작하기 (1) - 설정부터 실행까지](https://techblog.woowahan.com/2633/)