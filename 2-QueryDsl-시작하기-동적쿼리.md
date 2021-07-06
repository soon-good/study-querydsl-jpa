# 2. QueryDsl 시작하기 - 동적쿼리

요약하자면 이렇다. QueryDsl 에서의 동적쿼리는 `BooleanBuilder`, `BooleanExpression` 을 활용해서 여러가지 조건식을 메서드에 조합해 하나로 묶을 수 있다. 이렇게 하면, 하나의 조건식이 제품의 어떤 측면을 정확하게 설명하고 재사용하게끔 하는 재사용성을 늘려준다는 점에서 장점을 지니는 것 같다.<br>

QueryDsl에서 동적 쿼리를 사용할 때, 아래와 같은 클래스들을 사용한다.<br>

- BooleanBuilder
- BooleanExpression

<br>

## BooleanBuilder

- BooleanBuilder 에 조건식 BooleanExpression 을 넣어준다.
- 파라미터가 null 이 아닐 경우에 한해 `eq()` , `gt()` , `goe()` , `lt()` , `loe()` 와 같은 비교표현식이 가능하다.

예를 들어 아래와 같은 메서드 `searchMemberByBuilder(String, Integer)`  가 있다고 해보자.

```java
private List<Member> searchMemberByBuilder(String usernameCond, Integer ageCond){
  BooleanBuilder builder = new BooleanBuilder();

  if(usernameCond != null){
    builder.and(member.username.eq(usernameCond));
  }

  if(ageCond != null){
    builder.and(member.age.eq(ageCond));
  }

  return queryFactory.selectFrom(member)
    .where(builder)
    .fetch();
}
```

메서드를 자세히 보면, 이름과 나이를 기반으로 회원을 검색하고 있음을 알 수 있다.<br>

그리고 `BooleanBuilder` 객체 `builder` 내에는 BooleanExpression 들을 `and()` 메서드 내에 넣어서 하나씩 조합하고 있다.<br>

> 사실 `member.username.eq(usernameCond)` , `member.age.eq(ageCond)` 는 모두 Expression 을 반환하는 표현식이다.<br>

이렇게 만들어진 조건식인 builder를 where 절에 그대로 전달해주었다. 이렇게 querydsl 에서는 하나의 조건식을 builder 객체로 만들어서 변수화하는 것이 가능하다. 또는 builder 객체를 반환하는 메서드를 따로 만들어서 조건식을 재사용하는 것 역시 가능하다는 생각 역시도 든다.<br>

> 알기쉽게 요약하는게 오늘따라 힘들다. 나중에 두번째 정리할 때 한번 더 정리할 예정이다.ㅠㅜ 오늘은 일단 예제를 모두 정리해놓아야 할 듯 하다. 뭔가 딱 결론만 딱 짚어서 **"요게 요런거다"** 이렇게 설명할 수준이 아직은 아닌가보다 하는 생각이 든다.<br>

<br>

위에서 작성한 `searchMemberByBuilder`메서드를 where 절에 그대로 사용해보자.

```java
@SpringBootTest
@Transactional
public class QdslDynamicSqlBooleanBuilderTest{
  @Autowired
  EntityManager em;
  
  JPAQueryFactory queryFactory;
  
  // ...
  
  @Test
  public void dynamicSqlByBooleanBuilder1(){
    String username = "Genie";
    Integer age = null;
    
    List<Member> result = searchMemberByBuilder(username, age);
    assertThat(result.size()).isEqualTo(2);
  }

```



## BooleanExpression

위에서 살펴본 `member.username.eq(usernameCond)` , `member.age.eq(ageCond)` 은 `BooleanExpression` 을 반환하는 메서드이다. BooleanBuilder 내에는 여러가지 BooleanExpression 들을 Builder 패턴으로 조합할 수 있다. 이번에 살펴볼 내용은 BooleanExpression 을 이용한 다중 조건 조합 구문이다. if, else 구문으로 조건값을 처리할 때는 항상 그러했듯이 null 값 처리가 중요하다.<br>

<br>

### 동적 쿼리 조합 (1)

아래와 같이 querydsl의 eq 구문들을 이용해 BooleanExpressio 객체를 리턴하는 메서드들을 의미단위로 만들어놓았다고 해보자.

```java
private BooleanExpression ageEq(Integer pAge){
  return pAge == null ? null : member.age.eq(pAge);
}

private BooleanExpression userNameEq(String username){
  return userName == null ? null : member.username.eq(username);
}
```

<br>

이 경우, 아래와 같은 구문처럼 where 절에 추가해서 사용하는 것이 가능하다.

```java
private List<Member> searchData1(String username, Integer age){
  QMember member = QMember.member;
  return queryFactory
    .selectFrom(member)
    .where(userNameEq(username), ageEq(age))
    .fetch();
}
```

<br>

### 동적 쿼리 조합 (2)

BooleanExpression 객체들은 서로 and() 구문으로 연결해 합치는 것이 가능하다. 예제를 먼저 살펴보자.<br>

만약 아래와 같은 요구조건을 만족하는 회원들의 리스트를 뽑아내는 요구사항이 있다고 해보자. <br>

> - 나이가 29세 이하 
> - 비흡연자

<br>

비흡연자, 나이제한에 대한 표현식은 아래와 같이 `smokerTypeEq()` , `ageLoe()` 메서드로 따로 정의해두었다.

```java
private BooleanExpression smokerTypeEq(String smokerType) {
  return smokerType == null ? null : member.smokerType.eq(smokerType);
}

private BooleanExpression ageLoe(Integer age) {
  return age == null ? null : member.age.loe(age);
}
```

<br>

이렇게 정의한 메서드들을 하나의 메서드 내에서 또 모두 합쳐보려고 한다. 메서드 이름은 `isVIPMember(String smokerType, Integer age)` 이다. (29세 이하, 비흡연자는 VIP 회원이라는 제품의 요구조건이다)<br>

```java
private BooleanExpression isVIPMember(String smokerType, Integer age){
  return smokerTypeEq(smokerType).and(ageLoe(age));
}
```

위에서 보듯이, `smokerTypeEq() `, `ageLoe()` 메서드의 조건식을 하나로 합친 `isVIPMember()` 메서드를 따로 만들었다. 

<br>

아래는 위의 `isVIPMember(String, Integer)` 를 이용해 실제 쿼리를 수행해보는 예제이다.

```java
import static com.study.qdsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QdslMultiWhereParamTest{
  @Autowired
  EntityManager em;
  
  JPAQueryFactory queryFactory;
  
  // ...
  
  @BeforeEach
  public void before(){
    // 데이터 생성 구문은 생략
  }
  
  @Test
  public void dynamicSqlByMultiWhereParamTest2(){
    String smokerType = "NON_SMOKER";
    Integer age = 29;
    
    List<Member> data = searchData2(smokerType, age);
    assertThat(data.size()).isEqualTo(1);
  }
  
  private List<Member> searchData2(String smokerType, Integer age){
    QMember member = QMember.member;
    return queryFactory
      	.selectFrom(member)
      	.where(isVIPMember(smmokerType, age))	// 이 부분 주목
      	.fetch();
  }
  
  private BooleanExpression isVIPMember(String smokerType, Integer age){
    return smokerTypeEq(smokerType).and(ageLoe(age));
  }
  
  private BooleanExpression smokerTypeEq(String smokerType) {
		return smokerType == null ? null : member.smokerType.eq(smokerType);
	}
  
  private BooleanExpression ageLoe(Integer age) {
		return age == null ? null : member.age.loe(age);
	}
  
}
```


