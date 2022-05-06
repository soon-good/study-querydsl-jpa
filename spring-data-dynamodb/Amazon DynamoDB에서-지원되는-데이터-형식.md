# Amazon DynamoDB 에서 지원되는 데이터 형식

> 공식 문서 : [지원되는 데이터 형식](https://docs.aws.amazon.com/ko_kr/amazondynamodb/latest/developerguide/DynamoDBMapper.DataTypes.html)<br>

`BigDecimal` 타입을 사용해야 해서 지원 자료형을 찾아보던 차에 확인하게 된 공식문서. 정리시작.

<br>

## 기본, 참조타입 지원 여부

AmazonDB에서 지원하는 자바의 기본타입 or 기본 래퍼 클래스는 아래와 같다.

- `String`
- `Boolean` , `boolean`
- `Byte`, `byte` 
- `Date`([ISO_8601](http://en.wikipedia.org/wiki/ISO_8601) 밀리초 정밀도 문자열에 따라 UTC로 전환)
- `Calendar`([ISO_8601](http://en.wikipedia.org/wiki/ISO_8601) 밀리초 정밀도 문자열에 따라 UTC로 전환)
- `Long`, `long`
- `Integer`, `int`
- `Double`, `double`
- `Float`, `float`
- `BigDecimal`
- `BigInteger`

<br>

## Set, List, Map 등 컬렉션 지원 여부

DynamoDB는 Java [Set](http://docs.oracle.com/javase/6/docs/api/java/util/Set.html), [List](http://docs.oracle.com/javase/6/docs/api/java/util/List.html) 및 [Map](http://docs.oracle.com/javase/6/docs/api/java/util/Map.html) 컬렉션 형식을 지원한다. <br>

| Java 형식                                                    | DynamoDB 형식                                                |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| 모두 숫자 형식                                               | `N`(숫자 형식)                                               |
| 문자열                                                       | `S`(문자열 형식)                                             |
| 부울                                                         | `BOOL`(부울 형식), 0 또는 1                                  |
| ByteBuffer                                                   | `B`(이진수 형식)                                             |
| 날짜                                                         | `S`(문자열 형식) 날짜 값은 ISO-8601 포맷 문자열로 저장됩니다. |
| [Set](http://docs.oracle.com/javase/6/docs/api/java/util/Set.html)(집합) 컬렉션 형식 | `SS`(문자열 집합) 형식, `NS`(숫자 집합) 형식, 또는 `BS`(이진수 집합) 형식 |

<br>

## 데이터 변환 

예를 들면 `LocalDateTime` 과 같은 자료형은 아마존 SDK에서는 대부분 통하지 않는다. SDK의 목적상, 여러 사용자 층을 아울러야 한다는 점으로 인해, 자바 8 이하의 버전에서도 호환되게끔 하느라 `LocalDateTime` 이 호환되지 않는것 같다.<br>

이런 문제로 인해 Amazon DynamoDB 에서는 `DynamoDBTypeConverter` 라는 클래스를 제공해 사용자가 사용하고 있는 데이터 형식을 Amazon DynamoDB의 타입에 맞게 매핑할 수 있는 기능을 제공하고 있다.<br>

이것과 관련한 예제는 [DynamoDB 날짜 타입 변환 (Date <-> LocalDateTime)](https://github.com/soon-good/study-querydsl-jpa/blob/develop/spring-data-dynamodb/DynamoDB-%EB%82%A0%EC%A7%9C%ED%83%80%EC%9E%85-%EB%B3%80%ED%99%98-(Date-LocalDateTime).md) 에서 따로 정리해두었다.<br>

<br>