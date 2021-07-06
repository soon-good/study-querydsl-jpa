# QueryDSL Bulk Insert

querydsl 의 경우 update, delete 연산에서 영속성 계층을 거치지 않고 Database에 바로 커밋을 하는 execute 구문이 있다. 이때 entityManager 에 대해 flush,clear 연산을 수행해야 한다. 하지만, insert 의 경우는 querydsl 역시 별다른 대안이 없는가 보다. 이것 저것 자료를 찾아보니 또 [이분의 블로그](https://jojoldu.tistory.com/558)를 찾게되었다.<br>

오늘 요약할 내용은 Bulk Insert 를 Querydsl 에서 사용하는 방식이다. Querydsl 의 경우 오픈소스이고 안정화되지 않았는데, Bulk Insert 지원역시 미비한 상황이었다. 최근 Bulk Insert 가 지원되는 새로운 라이브러리가 생긴것으로 보인다.<br>

<br>

## 참고자료

- [Auto Increment 에서 TypeSafe Bulk Insert 진행하기](https://jojoldu.tistory.com/558)

<br>

## TODO

