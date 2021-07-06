# 3. QueryDsl 시작하기 - 페이징

페이징은 Spring Data 에서 제공해주는 Pageable, PageRequest 를 사용하는데, 꽤 직관적인 편이어서 이해가 쉽다. 하지만 한번 봐서는 까먹고, 몇달 이상 안쓰다보면 또 까먹기에 예제 기반의 정리문서를 하나 만들어두려 한다. 

<br>

## 참고자료

- [실전! Querydsl](https://www.inflearn.com/course/Querydsl-%EC%8B%A4%EC%A0%84/dashboard)

<br>

## Overview

강의에서는 리포지토리 계층부터 예제로 설명하고 있다. 하지만, 너무 리포지터리 계층에 집착해서 하는 것 말고, 컨트롤러 계층에서 먼저 예제를 살펴본 후에 리포지터리 계층에서도 어떻게 사용하는지 살펴보는 것도 나쁘지 않겠다는 생각이 들었다. 

<br>

## 컨트롤러 계층

### 웹 파라미터 바인딩

아래와 같은 컨트롤러가 있다고 해보자.

```java
package com.study.qdsl.web.member;

// ...
@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberJpaQdslRepository repository;
	private final MemberDataJpaRepository dataRepository;
  
	// ...
	
  @GetMapping("/v2/members")
	public Page<MemberTeamDto> getAllMember2(MemberSearchCondition condition, Pageable pageable){
		return dataRepository.searchPageSimple(condition, pageable);
	}
  
	// ...
  
}
```

<br>

위 컨트롤러에 대한 요청은 아래의 URL에 대한 요청이다.<br>

> [http://localhost:8080/v2/members?page=0&size=2](http://localhost:8080/v2/members?page=0&size=2)

<br>

컨트롤러가 핸들링하는 `/v2/members?page=0&size=2` url 에 대한 파라미터를 `@GetMapping` 으로 처리하는 메서드를 `getAllMember2()` 메서드라고 하자. <br>

이 때 `getAllMember2()` 메서드 내의 인자로 다른 어노테이션 없이 Pageable 타입의 객체 `pageable` 을 선언하면, 스프링 내부적인 동작으로 Pageable 객체를 생성한 후에 파라미터 중 `page`, `size` 의 값을 하나씩 관련 필드에 바인딩해주게 된다.<br>

<br>

이렇게 선비처럼 글로 설명하면 조금 이상하다. 역시 디버깅한 결과를 캡처로 떠놓는게 훨씬 나을수도 있겠다.

![이미지](https://github.com/gosgjung/study_archives/raw/master/java/queryDsl/ch06/img/PAGEABLE_AT_CONTROLLER.png)

<br>

위의 결과화면은 웹 요청이 왔을때 해당 시점에 브레이크 포인트를 걸어서 데이터를 확인해본 결과다.<br>

### JSON 응답형식 & SQL

#### (1) page = 0, size = 2

http://localhost:8080/v2/members?page=0&size=2 같은 요청 URL 로 요청을 했을때의 리턴 결과는 아래와 같다.<br>

**JSON 응답**<br>

![이미지](https://github.com/gosgjung/study_archives/raw/master/java/queryDsl/ch06/img/POSTMAN_API_RESULT.png)

<br>

**SQL**

```sql
```

<br>

#### (2) page = 1, size = 2

아래는 [http://localhost:8080/v2/members?page=1&size=2](http://localhost:8080/v2/members?page=1&size=2) 으로 요청을 했을 대의 리턴 결과이다.<br>

**JSON 응답**<br>

![이미지](https://github.com/gosgjung/study_archives/raw/master/java/queryDsl/ch06/img/POSTMAN_API_RESULT.png)

<br>

**SQL**<br>

```sql
```

<br>

#### (3) page = 5, size = 2 

요청 URL 은 [http://localhost:8080/v2/members?page=5&size=2](http://localhost:8080/v2/members?page=5&size=2) 이다.<br>

- page 가 5 라는 것은 여섯번째 페이지를 가져온다는 것이고
- size 가 2 라는 것은 2개씩 페이지를 나누어서 구분하겠다는 의미이다.

<br>

![이미지](https://github.com/gosgjung/study_archives/raw/master/java/queryDsl/ch06/img/POSTMAN_API_RESULT3.png)

<br>

## QueryDsl 코드

Querydsl 에서 페이징을 사용할 때 아래의 메서드 들을 사용하게 된다.

- `fetchResults() : QueryResults <T>`
  - Querydsl 의 페이징 기능을 사용할 때는 fetch() 메서드 대신 fetchResults() 메서드를 사용한다.
  - QueryResults<T> 내의 `content` 는 결과 값의 본문을 의미한다.
  - QueryResults<T> 내의 `total` 은 결과 값의 본문을 의미한다.
  - fetchResults() 메서드를 사용하면 querydsl 내부적으로 두번의 쿼리를 수행한다. 이 때 수행되는 쿼리는 content 쿼리, count 쿼리이다.
  - count 쿼리의 경우 잘못 사용하면 좋지 않은 결과를 가져올 수 있다. 이런 이유로 조금 우회적인 방식으로 fetchResult()를 사용하지 않기도 한다. 이것에 대해서는 아래에 정리해 두었다.
  - 개인적으로 가장 제일 좋은 방식은 초기 설계시에 카운트 테이블을 하나 두는 등의 전략을 취하는 것이 가장 좋은 방식인 것 같다.
- `limit (long limit) : JPAQuery<T> `
  - 데이터를 몇 개 단위로 묶어서 페이징할 지를 결정한다.
  - 0... i ... n-1
  - 만약 30을 지정했다면 30단위로 결과를 나눈결과에 대한 여러개의 페이지가 생성된다.
  - JPAQuery<T> 를 리턴하는데, 이것은 메서드를 체이닝할 수 있도록 현재 쿼리객체를 리턴함을 의미한다.
- `offset (long offset) : JPAQuery<T>`
  - 몇 번째 페이지를 보여줄지 명시할 때 사용하는 변수이다.
  - 데이터를 limit 단위로 나누었을때 만들어진 페이지들 중 몇번째(offset) 페이지를 참조할지를 결정한다.
  - 0... i ... n-1
  - JPAQuery<T> 를 리턴하는데, 이것은 메서드를 체이닝할 수 있도록 현재 쿼리객체를 리턴함을 의미한다.

<br>

### 테스트 코드

**searchPageSimple(SearchCondition, Pageable)** <br>

```java
// ...
public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable){
		QueryResults<MemberTeamDto> fetchResults = queryFactory
			.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.username,
				member.age,
				member.team.id.as("teamId"),
				member.team.name.as("teamName")
			))
			.from(member)
			.leftJoin(member.team, team)
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetchResults(); // fetchResults() 를 사용하면 content 쿼리와 count 쿼리 두번을 호출한다.

		List<MemberTeamDto> results = fetchResults.getResults();
		long total = fetchResults.getTotal();
		return new PageImpl<MemberTeamDto>(results, pageable, total);
}
```

<br>

**코드 실행 테스트**

```java
@SpringBootTest
@Transactional
class MemberJpaCustomTest{
  
  @Autowired
  EntityManager em;
  
  @Autowired
  MemberDataJpaRepository dataJpaRepository;
  
  @BeforeEach
  public void before(){
    Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");
		Team nullTeam = new Team("NullTeam");

		em.persist(marketingTeam);
		em.persist(analysisTeam);
		em.persist(musicianTeam);
		em.persist(nullTeam);

		Member john = new Member("John", 23, marketingTeam);
		Member susan = new Member("Becky", 22, marketingTeam);

		Member kyle = new Member("Kyle", 28, analysisTeam);
		Member stacey = new Member("Stacey", 24, analysisTeam);

		Member aladin = new Member("Aladdin", 35, analysisTeam);
		Member genie = new Member("Genie", 41, analysisTeam);

		Member beethoven = new Member("Beethoven", 251, musicianTeam);
		Member chopin = new Member("Chopin", 210, musicianTeam);
		Member genie2 = new Member("Genie", 210, musicianTeam);
		Member nullName = new Member(null, 100, musicianTeam);

		Member ceo = new Member("Jordan", 49, null);

		em.persist(john);
		em.persist(susan);
		em.persist(kyle);
		em.persist(stacey);
		em.persist(aladin);
		em.persist(genie);

		em.persist(beethoven);
		em.persist(chopin);
		em.persist(genie2);
		em.persist(nullName);
		em.persist(ceo);
  }
  
  @Test
	public void searchPageSimpleTest(){
		MemberSearchCondition condition = new MemberSearchCondition();
    
    QMember member = QMember.member;
    
		// 스프링 데이터의 페이지네이션의 page 는 0번 부터 시작된다.
		PageRequest pageRequest = PageRequest.of(0, 3);
		Page<MemberTeamDto> results = dataJpaRepository.searchPageSimple(condition, pageRequest);

		assertThat(results.getSize()).isEqualTo(3);

		assertThat(results.getContent())
			.extracting("username")
			.containsExactly("John", "Becky", "Kyle");

		System.out.println("results === ");
		System.out.println(results);
	}
}
```

<br>

## 튜닝

사실 개발 초반에 제품의 기획적인 측면을 미리 알고있는 상태에서 고도화를 하는 것이라면 어떤 것이 카운트쿼리가 필요한지 등에 대해 미리 알고 있을 수 있다. 이런 경우에는 설계 초기 단계에서 카운트 테이블을 따로 만드는 것이 나을 수 있다. <br>

하지만, 오늘 다루는 문서에서는 설계측면이 아니라, 이미 존재하는 테이블에 카운트 쿼리를 날릴 경우 페이지네이션 적용시 튜닝하는 방식들에 대해 정리해보려 한다.

<br>

> 참고) <br>
>
> 사실 Spring에서 제공해주는 Pageable 등을 그대로 사용하는 회사는 없을것 같다. 대부분 독자적인 응답형식을 갖고있고, 응답 코드 역시도 자체적으로 정의해서 사용한다. 

<br>

### 1) Count 시 불필요한 Join 제거

대부분의 서비스들은 조회쿼리가 복잡한 경우가 많을 것 같다. 그리고, 데이터의 양이 방대하게 쌓여 있는 경우 역시 많을 것 같다. 이런 경우 조인과 동시에 Count 를 할 경우 부하가 심해진다.  이런 경우, 복잡하게 Join을 하지 않고, 단일 데이터 객체(테이블)의 행의 수만 들고 오면 되는 경우가 있다. (예를 들면메타 정보등을 가져오기 위해 left join 등의 구문을 사용하는 경우 역시도 부하가 심하다.)<br>

<br>

이 경우 Querydsl 이 반환해주는 fetchResults()를 그대로 사용하지 않고, fetch() 메서드로 `content` 를 가져오고, `total` 값은 count 쿼리를 새롭게 작성해 사용하는 것이 나은 선택이다.<br>

아래 예제에서는 이런 내용들을 반영해서 아래의 쿼리들을 각각 수행하도록 작성했다.<br>

```java
package com.study.qdsl.repository.custom;

//...

public class MemberJpaCustomImpl implements MemberJpaCustom {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public MemberJpaCustomImpl(EntityManager em){
		this.em = em;
		queryFactory = new JPAQueryFactory(em);
	}

  // ...
  
	@Override
	public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {

		List<MemberTeamDto> results = queryFactory.select(
			new QMemberTeamDto(
				member.id.as("memberId"),
				member.username.as("username"),
				member.age,
				team.id.as("teamId"),
				team.name.as("teamName")
			)
		)
		.from(member)
		.leftJoin(member.team, team)
		.where(
			userNameEq(condition),
			teamNameEq(condition),
			ageGoe(condition),
			ageLoe(condition)
		)
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();
    
		long count = queryFactory.select(member)
			.from(member)
//			.leftJoin(member.team, team) // 필요 없을 때도 있다.
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetchCount();

		return new PageImpl<MemberTeamDto>(results, pageable, count);
	}
  // ...
}
```

<br>

### 2) 선택적으로 count 쿼리 수행

페이징 수행시 count() 쿼리를 생략해도 되는 경우가 있다. 예를 들면 가장 마지막 페이지의 경우이다. 이럴 때는 count 쿼리 없이 Java 소스 레벨에서 List 의 size()를 통해 `total` 데이터를 구해낼 수 있다.<br>

예를 들면 아래의 경우들에 대해 count 쿼리를 선택적으로 수행하도록 적용할 수 있다.<br>

- 페이지의 시작이면서 컨텐츠의 사이즈가 페이지의 사이즈보다 작을때
  - ex) 페이지의 사이즈는 30개로 정했는데 DB에는 20개의 글 밖에 존재하지 않는 경우
- 가장 마지막 페이지의 데이터를 요청할 때
  - (offset x size) + 리스트.size() 를 통해 전체 사이즈를 구한다.
  - 즉, 카운트 쿼리 없이 요청정보와 결과 데이터만을 잘 조합하면 카운트 쿼리를 할 필요가 없다.

그리고 리턴 값은 PageableExecutionUtils::getPage() 메서드를 이용해 생성해서 리턴한다.<br>

```java
package com.study.qdsl.repository.custom;

// ...

public class MemberJpaCustomImpl implements MemberJpaCustom {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public MemberJpaCustomImpl(EntityManager em){
		this.em = em;
		queryFactory = new JPAQueryFactory(em);
	}

	// ...
  
	@Override
	public Page<MemberTeamDto> searchPageOptimized(MemberSearchCondition condition, Pageable pageable) {
		List<MemberTeamDto> results = queryFactory.select(new QMemberTeamDto(
			member.id.as("memberId"),
			member.username.as("username"),
			member.age,
			member.team.id.as("teamId"),
			member.team.name.as("teamName")
		))
		.from(member)
		.leftJoin(member.team, team)
		.where(
			userNameEq(condition),
			teamNameEq(condition),
			ageGoe(condition),
			ageLoe(condition)
		)
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();

		/** Query 를 람다 표현식에 저장 */
		JPAQuery<Member> countSql = queryFactory
			.select(member)
			.from(member)
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			);

		// SQL을 람다 표현식으로 감싸서 람다 또는 메서드 레퍼린스를 인자로 전달해준다.
		// PageableExecutionUtils 에서 위의 1),2) 에 해당하면 SQL 호출을 따로 하지 않는다.
//		return PageableExecutionUtils.getPage(results, pageable, ()->countSql.fetchCount());
		// or
		return PageableExecutionUtils.getPage(results, pageable, countSql::fetchCount);
	}
}
```

<br>

### 3) 카운트가 기획상에서 필요하지 않은 경우

기획상에서 제품에 페이징시 카운트가 필요없다고 결정지었을 경우 굳이 카운트 쿼리를 수행할 필요는 없다. 이 경우 fetchResults() 대신 fetch() 메서드를 호출해서 결과를 반환하면 된다.

