# 2. QueryDsl 시작하기 - 조인

> 혼자 사용하는 공간이지만, 혹시라도 이 글을 볼지도 모르는 분들을 위해 남깁니다<br>
>
> 베이스로 참고한 자료는 [실전! Querydsl](https://www.inflearn.com/course/Querydsl-%EC%8B%A4%EC%A0%84/dashboard) 입니다. 이것 저것 설명을 많이 붙일까 하다가 요약 위주로 정리합니다<br>
>
> 자세한 설명은 강의를 보셔야 제대로 이해하실 수 있습니다. <br>

<br>

오늘 정리할 내용은 조인 구문이다. 가급적 예제 기반으로 모두 정리했다. 실제 예제 프로젝트 소스코드는 하루 날잡고 한번에 싹 다 올려볼까 생각중이다. 예제 코드 역시 Employee, Department 기반의 관계로 변경할 예정이다. 테스트 코드 기반으로 하나씩 돌려보는 것은 얼마 안걸릴것 같긴 한데, 브랜치 만들고, 프로젝트 만들고, 테스트 용도 DB의 도커 컴포즈 파일을 올려주고 또 그건 설명문서 써야하고 ㅋㅋ <br>

이것 저것 손댈것 투성이라 손 댈 엄두도 못내고 있다 아직은ㅋㅋ  그래도 공부를 하는 이유가 앞으로 더 잘되고 싶어서 하는거니 불만은 없다 아직은....<br>

<br>

## 예제

### Member.java

```java
package com.study.qdsl.entity;

// ...

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id", "username", "age"})
public class Member {

	@Id @GeneratedValue
	@Column(name = "member_id")
	private Long id;
	private String username;
	private int age;

	// Team <-> Member 연관관계의 주인
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")	// 외래키 컬럼 명
	private Team team;

	public Member(String username){
		this(username, 0);
	}

	public Member(String username, int age){
		this(username, age, null);
	}
	
	public Member(String username, int age, Team team){
		this.username = username;
		this.age = age;
		if(team != null){
			changeTeam(team);
		}
	}

	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
}

```

<br>

### Team.java

```java
package com.study.qdsl.entity;

// ...

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id", "username", "age"})
public class Team {

	@Id @GeneratedValue
	private Long id;
	private String name;

	// Team <-> Member 의 연관관계를 당하는 입장 (거울...)
	@OneToMany(mappedBy = "team")
	private List<Member> members = new ArrayList<>();

	public Team(String name){
		this.name = name;
	}
}

```

<br>

### 예제 데이터 만들기

```java
package com.study.qdsl.ch03_basic_sql;
// ...
// ...
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
@Transactional
public class QdslSearchCondtionTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);

		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");

		em.persist(marketingTeam);
		em.persist(analysisTeam);
		em.persist(musicianTeam);

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
	}
}

```



## 조인구문 사용 기본예제

QueryDsl 에서 조인을 사용하는 기본적인 용례는 아래와 같다.

```java
QMember member = QMember.member;
QTeam team = QTeam.team;

List<Member> musicianMembers = queryFactory.selectFrom(member)
  .join(member.team, team)		// Member가 mapping하는 Team과 Team엔티티를 조인한다.
  // Join 기준은 Team.id 이며, 여기서는 묵시적으로 지정되었다.
  .where(team.name.eq("Musician"))
  .fetch();
```

위의 예제에서는 Member의 team_id 와 Team의 id를 연관지어서 조인하고 있다. fetch() 는 list와 같은 타입을 결과값으로 하는 조인을 할 때 사용하는 메서드이다.<br>

<br>

## join() 

조인시 `inner join`,  `left join`,  `right join` 등의 조인 종류를 지정하지 않고 join() 메서드 하나만 호출하는 경우를 기본 조인이라고 한다. 이 경우 inner join SQL 이 생성된다.<br>

<br>

**예제) Member 테이블 내에서 팀 이름이 'Musician'인 팀원들의 팀이름, 팀 id, 멤버 id, 멤버가 속한 팀 id, 멤버 이름을 출력**

```java
QMember member = QMember.member;
QTeam team = QTeam.team;

List<Member> musicianMembers = queryFactory.selectFrom(member)
  .join(member.team, team)
  .where(member.team.name.eq("Musician"))
  .fetch();
```

<br>

생성된 SQL

```sql
select member1
from Member member1
  inner join member1.team as team
where member1.team.name = 'Musician'
```

<br>

## leftJoin()

```java
final QMember member = QMember.member;
final QTeam team = QTeam.team;

List<Member> jordanTeam = queryFactory.selectFrom(member)
  .leftJoin(member.team, team)
  .where(member.username.eq("Jordan"))
  .fetch();
```

<br>

생성된 SQL

```sql
select 
	member0_.member_id 	as member_i1_1_, 
	member0_.age 				as age2_1_, 
	member0_.team_id 		as team_id4_1_, 
	member0_.username 	as username3_1_ 
from member member0_ 
left outer join team team1_ 
	on member0_.team_id=team1_.id 
where member0_.username=?
```



## innerJoin()

### on()

innerJoin() 예제를 살펴보기에 앞서서 `on()` 절을 알아보고 넘어가야 할 것 같다. on() 절은 JPA2.1 부터 지원하기 시작했다. on() 절을 사용하는 것은 주로 아래의 두 경우에 사용하게 된다.<br>

- 조인 대상을 필터링해야 할 때(조인하기 전에 조인할 테이블을 미리 필터링해서 가져오는 경우)
- 연관관계가 없는 엔티티 간에 외부조인을 할 경우

<br>

on() 절을 활용한 조인 대상 필터링을 사용할 때 주의해야 할 사항들을 정리해보면 아래와 같다/.

- innerJoin 인데 기본 키 매핑 관계를 기반으로 조인할 경우
  - querydsl 코드에서는 on() 을 사용하지 않아도 된다. where() 로도 해결된다.
  - SQL을 직접 작성할 경우는 반드시 on 절을 명시해야 한다.
- innerJoin 인데 기본 키 매핑 관계가 아닌 다른 얀관관계로 조인할 경우 (ex. 팀이름 등등...)
  - queryDsl 코드에서 on() 절을 통해 조인할 대상을 명시적으로 지정한다.
  - SQL을 직접 작성할 경우 반드시 on 절을 명시한다.
- left outer join, right outer join 등의 경우에는 기본키 외의 다른 키를 매핑할 경우, 반드시 on 절이 필요하다.

<br>

### innerJoin()

예) on 절에 아무 조건도 놓지 않았을 경우에 대한 테스트 코드

```java
@Test
public void innerJoinTest(){
  //		on 절에 아무조건도 놓지 않았을 경우에 SQL이 어떻게 표현되는지 확인해보기
  QMember member = QMember.member;
  QTeam team = QTeam.team;

  List<Member> fetch = queryFactory.selectFrom(member)
    .innerJoin(member.team, team)
    .fetch();
}
```

결과) 실제 쿼리는 QueryDsl 에서 아래와 같이 on 절을 직접 넣어준다. 엔티티의 기본키를 넣고 맞춰준다.

```sql
select 
	member0_.member_id as member_i1_1_, 
	member0_.age as age2_1_, 
	member0_.team_id as team_id4_1_, 
	member0_.username as username3_1_ 
from member member0_ 
inner join team team1_ 
	on member0_.team_id=team1_.id;
```

혼동이 될 수 있는 사항에 대해 짚고 넘어가야 할 것 같다.

queryDsl 자바코드와 SQL의 코드가 완전히 일치하는 것이 아니고, QueryDsl에서 옵셔널 하게 키를 매핑해서  Query를 맞춰주는 부분이 있다. 프로그래머가 어떠한 조인을 작성했을 때 해당 조인의 조인 조건이 없을 경우 디폴트로 기본  엔티티 매핑을 활용해 조인을 한다. 예를 들어 위의 on()을 사용하지 않은 inner join구문을 SQL에서 비슷하게  입력해보면 결과가 아래와 같이 cross join과 같은 결과가 나타난다. (SQL 사용시 on 절을 무조건 생략할 수있는  것이라고 착각하기 쉽기 때문에 메모를 남긴다. java 코드에서 on()이 생략될 경우 queryDsl에서 on절을 맞춰주는것.)

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch03/img/INNER_JOIN_JAVA_VS_SQL.png)

<br>

INNER JOIN이 아닌 기본 조인의 경우에도 SQL상에서 ON절을 명시하지 않으면 결과는 같다.

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch03/img/JOIN_JAVA_VS_SQL.png)



### leftJoin(), rightJoin() ~ on()

SQL의 LEFT (OUTER) JOIN, RIGHT (OUTER) JOIN 은 ON 절을 사용한다. QueryDsl에서  left outer join, right outer join 등의 경우에는 기본키 외의 다른 키를 매핑할 경우에 반드시 on()이 반드시 필요하다.

- leftJoin()
  - LEFT (OUTER) JOIN
- rightJoin()
  - RIGHT (OUTER) JOIN

ex) left (outer) join

```java
@Test
public void leftJoin(){
  QMember member = QMember.member;
  QTeam team = QTeam.team;

  List<Member> analysisMembers = queryFactory.selectFrom(member)
    .leftJoin(member.team, team)
    .where(member.team.name.eq("Analysis"))
    .fetch();

  assertThat(analysisMembers)
    .extracting("username")
    .containsExactly("Kyle","Stacey","Aladdin","Genie");
}
```

변환된 SQL

```sql
select
	member0_.member_id as member_i1_1_,
	member0_.age as age2_1_,
	member0_.team_id as team_id4_1_,
	member0_.username as username3_1_
from
	member member0_
	left outer join team team1_
		on member0_.team_id=team1_.id
where
	team1_.name='Analysis'
```

<br>

## fetchJoin()

> 참고자료 : [https://yellowh.tistory.com/133](https://yellowh.tistory.com/133) <br>
>
> - 난 강사님의 fetchJoin() 설명을 도저히 이해할수가 없었다. 오히려 위 블로그의 글을 보고 이해가 갔다.

<br>

- fetchJoin() 은 SQL 조인으로 서로 연관관계로 매핑한 엔티티들을 SQL 조회 한번에 한꺼번에 데이터를 가져올 때 사용한다. 주로 성능최적화에 사용하는 편이다.
- 엔티티의 연관관계를 지을 때 `FetchType.LAZY` 를 주었다면, 매핑관계의 엔티티를 한번에 불러오지 않기 때문에 fetchJoin()을 사용하게 되는 경우가 있다. ( `FetchType.LAZY`  는 `@OneToMany` 와 같이 Join으로 불러올 대상이 많은경우 잦은 조인이 성능을 저하의 우려가 있어서 `FetchType.LAZY` 를 사용하기도 한다.)
- `fetchJoin()`  을 테스트할 때에는 영속성 컨텍스트의 값을 제때 지워주어야 결과를 확인하기 쉽다. 따라서 영속성 컨텍스트 내의 값을 자주 flush(), clear() 해주어야 한다. (영속성 컨텍스트에 내가 불러온 값이 제대로 들어왔는지 확인해봐야 하기에 엔티티 캐시를 자주 비워주는것.)
- 난 실제 SQL에 있는줄 착각한적이 있었다.ㅋㅋ.... 그냥... JPQL 에서도 사용할 수 있는 조인이고, 객체지향 기반 쿼리를 사용할 때 사용할 수 있는 개념이다. (only 자바/스프링 진영에서만 이해할 수 있는 용어인 듯 하다. 이런 점은 좀 마음에 안들긴 한다.)
- 객체지향 쿼리라는 것은 결국 객체들 안에 데이터를 넣어주어야 하는 것을 의미한다. 보통 일반 조인의 경우는 서로 연관된 두개 이상의 객체를 서로 조인해서 결과를 낼때 Dto 에는 값을 바인딩 할 수 있다. 하지만 엔티티의 속까지 한번에 업데이트 하지는 못한다.
- `fetchJoin()` 은 이렇게 엔티티 내의 내용까지 한번에 업데이트 해준다. 조인 한방에 모든 실제 엔티티 객체내의 값 까지 싹다 리프레쉬해주는 개념이다. 

<br>

### 예제) fetchJoin() 사용하지 않을 경우

Member엔티티 내에 Team 클래스에 대한 결과가 제대로 불러져 오는지 확인해보기

```java
@SpringBootTest
@Transactional
public class QdslJoinFetchTest{

	@Autowired
	EntityManager em;

  // J2EE 환경에서는 @PersistenceUnit을 통해 EntityManagerFactory 인스턴스를 얻을 수 있다.
	@PersistenceUnit
	EntityManagerFactory emf;

	JPAQueryFactory queryFactory;
  
  // .... 

	/**
	 * Member 엔티티의 @JoinColumn 으로 지정해준 Team 필드는 @ManyToOne 이 걸려 있는데
	 * @ManyToOne의 fetch 의 타입이 FetchType.LAZY 로 지정되어 있다.
	 *
	 * FetchType 이 lazy 이기 때문에 DB에서 조회시 Member만 조회되고 Team은 조회되지 않는다.
	 */
	@Test
	public void nonFetchJoin(){
		em.flush();
		em.clear();

		QMember member = QMember.member;
		QTeam team = QTeam.team;

		Member chopin = queryFactory.selectFrom(member)
			.where(member.username.eq("Chopin"))
			.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(chopin.getTeam());

		System.out.println(" ======= Chopin's Team? ======= ");
		System.out.println(" >>> loaded ? " + loaded);
//		System.out.println(" >>> " + chopin.getTeam());	// chopin.getTeam() 을 하면 SQL을 한번더 호출한다!!

		assertThat(loaded).as("페치(Team을 가져왔는지)되었는지 체크 - isLoaded ?? >>> ").isFalse();
	}
  
  // ... 
  
}
```

<br>

### 출력결과

```plain
 ======= Chopin's Team? ======= 
 >>> loaded ? false
 >>> chopin ? Member(id=12, username=Chopin, age=210)
```

<br>

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch03/img/DEBUG_NON_FETCH_JOIN_1.png)

<br>

- `chopin.name` 은 null 이다.
- `chopin.id` 는 null 이다.
- `members: ArrayList<Member> ` 는 비어있는 리스트이다.

<br>

### 연관관계 인스턴스 참조시의 동작

> System.out.println(">>>" + chopin.getTeam()).

위와 같은 구문을 추가해서 위의 구문을 호출할 때 SQL을 보내는지 확인해보자. <br>

호출 전의 `chopin`이라는 username 을 가진 인스턴스의 데이터는 아래와 같은 모습이다.<br>

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch03/img/DEBUG_NON_FETCH_JOIN_2.png)

<br>

이제 이 다음 스텝으로 `System.out.println(">>>" + chopin.getTeam());` 을 실행할 때 아래와 같이 SQL 이 호출된다. (자세히 살펴보면 해당 인스턴스에 팀의 정보를 조회하기 위한 단건 조회를 수행하는 것임을 알 수 있다.)<br>

<br>

```plain
2020-03-30 08:37:43.415 DEBUG 26682 --- [    Test worker] org.hibernate.SQL                        : 
    select
        team0_.id as id1_2_0_,
        team0_.name as name2_2_0_ 
    from
        team team0_ 
    where
        team0_.id=?
...
...
select team0_.id as id1_2_0_, team0_.name as name2_2_0_ from team team0_ where team0_.id=3;
 >>> Team(id=3)
```

<br>

이렇게 되는 이유는 Entity 에 FetchType.Lazy를 적용해서 지연로딩을 강제했다. 하지만, 특정 로직에서는 FetchType.EAGER 와 같은 동작을 수행해야만 할 경우가 있다. 위의 경우는 이미 기 적용된 FetchType.Lazy를 따라서 실행된 것이기에 필요한 정보만 단건 조회를 수행하게 되었다.

<br>

아래에서부터는 FetchType.Lazy 를 적용해서 지연로딩이 적용된 필드를, 특정 SQL 구문에서만 FetchType.EAGER 타입으로 조인을 통한 즉시로딩을 하도록 변경한 예제를 살펴본다.

<br>

### 예제) fetchJoin() 을 사용할 경우 

- 아래 예제를 자세히 보면 join(...) 구문 뒤에 .fetchJoin()을 한번 더 호출해주었다.
- 뭔가  `join() 을 호출해놓은것을 바로 fetch 하겠다.` 이런 의미처럼 보인다.

<br>

```java
@Test
public void useFetchJoin(){
  em.flush();
  em.clear();

  QMember member = QMember.member;
  QTeam team = QTeam.team;

  Member genie = queryFactory.selectFrom(member)
    .join(member.team, team).fetchJoin()	// 이 부분이 변경되었다.
    .where(
    member.username.eq("Genie")
    .and(member.age.eq(41))
  )
    .fetchOne();

  boolean loaded = emf.getPersistenceUnitUtil().isLoaded(genie.getTeam());

  assertThat(loaded).as("페치(Team을 가져왔는지)되었는지 체크 - isLoaded ?? >>> ").isTrue();
}
```

<br>

위 구문에 대한 SQL 결과는 아래와 같다. 실제로, fetch가 강제화 되어서 조인이 호출된다.<br>

Member 엔티티 내의 Team 객체 변수인 `team` 은 `FetchType.Lazy` 로 지정해놓은 상태이다. 이런 이유로 JPQL이나 querydsl, Data JPA 의 쿼리 메서드를 이용해 Memeber 테이블을 단순 조회할 때 `Member.team` 객체가 비어있는 채로 결과를 돌려받게 된다. <br>

하지만 FetchJoin 을 하게 되면 `Memeber.team`안에 값을 업데이트 한다. 그리고, 실제 영속 컨테이너(=메모리 내의 엔티티 버전관리 컨테이너)에도 엔티티의 값을 업데이트 치게 된다.<br>

> 일단 QueryDsl 의 모든 다른주제에 대해서 하니씩 모두 다 정리하고 다시 돌아와서 Fetch Join에 대해 예제를 더 좋은 걸 예로 들어서 정리해봐야 할 것 같다. 강의로 전달받은 예제는 요점이 너무 없다. 그리고, Fetch Join 강의 부분은 좀... 어느 정도는 너무 횡설수설이여서... 나 뿐만 아니라 불특정 다수의 이 글을 읽을 지도 모르는 사람들에게 혼동을 주게되는 것 같다.<br>
>
> 직접 인터넷을 찾아보면서 괜찮다고 생각한 자료는 [여기](https://yellowh.tistory.com/133) 였다.

<br>

```plain
select
            member0_.member_id as member_i1_1_0_,
            team1_.id as id1_2_1_,
            member0_.age as age2_1_0_,
            member0_.team_id as team_id4_1_0_,
            member0_.username as username3_1_0_,
            team1_.name as name2_2_1_ 
        from
            member member0_ 
        inner join
            team team1_ 
                on member0_.team_id=team1_.id 
        where
            member0_.username=? 
            and member0_.age=?
2020-03-30 09:05:35.865  INFO 26767 --- [    Test worker] p6spy                                    : #1585526735865 | took 0ms | statement | connection 3| url jdbc:h2:tcp://localhost/~/querydsl
/* select member1
from Member member1
  inner join fetch member1.team as team
where member1.username = ?1 and member1.age = ?2 */ select member0_.member_id as member_i1_1_0_, team1_.id as id1_2_1_, member0_.age as age2_1_0_, member0_.team_id as team_id4_1_0_, member0_.username as username3_1_0_, team1_.name as name2_2_1_ from member member0_ inner join team team1_ on member0_.team_id=team1_.id where member0_.username=? and member0_.age=?
/* select member1
from Member member1
```

<br>

## 크로스조인

여기부터 이어서 다시 정리 시작!!!

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch03/img/CROSS_JOIN.png)

