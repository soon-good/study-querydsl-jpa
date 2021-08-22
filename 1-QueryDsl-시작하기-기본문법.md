# QueryDsl 시작하기

> **혹시라도 이 글을 보실 블특정 다수 익명의 사용자분들께.**<br>
>
> 베이스로 참고한 자료는 [실전! Querydsl](https://www.inflearn.com/course/Querydsl-실전/dashboard) 의 **섹션 3. 기본문법** 입니다. 이것 저것 설명을 많이 붙일까 하다가 요약 위주로 정리합니다. 자세한 설명은 강의를 보셔야 제대로 이해하실 수 있습니다. 

<br>

## QType 생성하기

Gradle 빌드 툴에 querydsl 빌드 용도의 태스크를 other에 등록해두었다면 그 빌드를 실행하고 나면, QType이 생성된다. 나의 경우는 compileJava 에서 실행되도록 했었다. 예전에 정리했던 문서인 [github/gosgjung/study_archives](https://github.com/gosgjung/study_archives/blob/master/java/queryDsl/ch03/1_QType%EC%9D%98_%EA%B0%9C%EB%85%90_%EB%B0%8F_%ED%99%9C%EC%9A%A9.md) 에서는 gradle 4.x 대에서의 설정이었고 compileQueryDsl 을 사용했다.<br>

아무튼 어떤 방식으로든 Querydsl 빌드 태스크를 만들어둔 상태라면 해당 빌드를 수행한다. 

ex) gradle 4.x

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch03/img/GRADLE_TASK_OTHER.png)

이렇게 하고나면 보통 QType 이라는 불리는 .class 파일이 생성된다. <br>

빌드를 수행하고 나면, `@Entity` 가 붙은 모든 클래스에 대해 QMember, QTeam 처럼 Q가 앞에 붙은 쿼리를 위한 클래스 타입이 새로 generated 디렉터리 내에 생성된다.<br>

<br>

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch03/img/GENERATED.png)

<br>

## 기본 엔티티 매핑

따로 개념설명을 남겨두진 않으려 한다. 가장 많이 쓰는 @ManyToOne 에 대한 예제이다.<br>

### Member.java

```java
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member{
  @Id @GeneratedValue
  @Column(name="member_id")
  private Long id;
  private String username;
  private int age;
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="team_id")
  private Team team;
  
  public Member(String name, int age, Team team){
    this.username = username;
    this.age = age;
    if(team != null){
      changeTeam(team);
    }
  }
}
```

<br>

### Team.java

```java
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id", "username", "age"})
public class Team{
  
  @Id @GeneratedValue
  private Long id;
  private String name;
  
  @OneToMany(mappedBy="team") 
  private List<Member> members = new ArrayList<>();
  
  public Team(String name){
    this.name = name;
  }
}
```





## SpringBoot 에서 QueryDsl 기반 샘플 테스트코드 작성

세팅을 마쳤으면 테스트를 해봐야 한다. QueryDsl 기반의 SQL 코드는 JPAQueryFactory 타입의 인스턴스로 생성한다. 이 JPAQueryFactory 객체는 EntityManager 객체를 받아서 쿼리를 만들어내는 역할을 한다. 

```java
@SpringBootTest
public class QueryDslHelloTest{
  @Autowired
  EntityManager em;
  
  JPAQueryFactory queryFactory;
  
  @BeforeEach
  public void init(){
    queryFactory = new JPAQueryFactory(em);
  }
}
```

이렇게 해서 생성된 queryFactory 라는 JPAQueryFactory 타입의 인스턴스로 QueryDSL의 여러가지 함수를 이용해 SQL을 프로그래밍 적으로 생성할 수 있다.<br>

Querydsl 에서는 아래와 같이 Select 를 수행한다.<br>

```java
List<Member> members = queryFactory.select(QMember.member)
  .from(QMember.member)
  .fetch();
```

<br>

전체 예제는 아래와 같다.<br>

```java
@SpringBootTest
@Transactional
public class QueryDslTest{
  @Autowired
  EntityManager em;
  
  JPAQueryFactory queryFactory;
  
  // 샘플 데이터 생성
  @BeforeEach
  public void init(){
    queryFactory = new JPAQueryFactory(em);
    Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");

		entityManager.persist(marketingTeam);
		entityManager.persist(analysisTeam);
		entityManager.persist(musicianTeam);

		Member john = new Member("John", 23, marketingTeam);
		Member susan = new Member("Becky", 22, marketingTeam);

		Member kyle = new Member("Kyle", 28, analysisTeam);
		Member stacey = new Member("Stacey", 24, analysisTeam);


		entityManager.persist(john);
		entityManager.persist(susan);
		entityManager.persist(kyle);
		entityManager.persist(stacey);    
  }
  
  @Test
	public void selectAll(){
		List<Member> members = queryFactory.select(member)
			.from(member)
			.fetch();

		for(Member m : members){
			System.out.println( " member :: " + m);
		}
	}
}
```

<br>

## QType("별칭") - QType에 별칭지정하기

아래와 같이 QType 의 생성자 내에 variable 필드에 대해 문자열을 할당과 동시에 객체를 생성하면 해당 테이블의 별칭으로 지정된다. 셀프 조인 등을 할때 자기 자신의 테이블을 조인을 해야 하는데, 이러한 경우에 유용하게 사용할 수 있다.

ex)

```java
@Test
public void startQueryDsl(){
  // 여기서 테이블의 별칭을 지정했다.
  QMember m1 = new QMemeber("m1");
  
  Member findMember = queryFactory
    .select(m1)
    .from(m1)
    .where(m1.username.eq("member1"))
    .fetchOne();
  
  assertThat(findMember.getUsername()).isEqualTo("member1");
}
```

<br>

## where 구문 - 검색조건 적용

조회 구문에 검색 조건을 적용해서 검색 결과를 추려낼 때에는 `and()` , `or()` 를 사용해 조건을 연결해서 검색조건식을 만들어낸다. <br>

조금 더 응용하게 되면 BooleanExpression 을 반환하는 여러가지 메서드를 여러개 나열하면서 체이닝하여 사용할 수 있는 경우도 있다. <br>

```java
// ...
@Test
public void search(){
  QMember member = QMember.member;

  Member selectedMember = queryFactory.select(member)
    .from(member)
    .where(
    member.username.eq("Aladdin")
    .and(member.age.between(30, 40))
  )
    .fetchOne();

  assertThat(selectedMember.getUsername()).isEqualTo("Aladdin");
}
// ...
```

<br>

**ex) 멤버들 중 이름이 Aladdin 이면서, 나이가 35인 멤버 찾기**<br>

BooleanExpression 을 반환하는 함수를 여러개 나열하는 방식

```java
// ...
@Test
public void searchAndParam(){
  final QMember member = QMember.member;

  Member memberResult = queryFactory.selectFrom(member)
    .where(
    member.username.eq("Aladdin"),
    member.age.eq(35)
  )
    .fetchOne();

  assertThat(memberResult.getUsername()).isEqualTo("Aladdin");
}
// ...
```

<br>

## fetch() - 결과조회

select() 로 얻어온 JPAQuery\<T\> 를 from(), where(), join() 등의 구문을 계속 이어가면서 쿼리를 만들어내는데, 모두 만들어진 SQL 구문뒤에  `fetch()` 메서드를 호출하면, 결과값을 반환하게 된다.

ex 1) fetch() 를 호출하지 않은 경우 - JPAQuery\<T\> 를 반환한다.

```java
JPAQuery<Member> queryContext = 
  	queryFactory
  		.select(...)			// 얻어온 Entity 들 중에서 어떤 컬럼(필드)을 보일지 기술
  		.from(...)				// Entity 에서 얻어온다.
  		.where(...)				// 어떤 Entity에서 데이터를 가져올지 기술
  		.join(...)				// join을 어떤 Entity와 할지 기술
```

<br>

ex 2) fetch() 호출하기

```java
List<Member> results = queryContext.fetch();
```

<br>

ex 3) 모든 조건 검색식을 이어서 SQL 구문작성하기

```java
List<Member> results = 
  	queryFactory
  		.select(...)			// 얻어온 Entity 들 중에서 어떤 컬럼(필드)을 보일지 기술
  		.from(...)				// Entity 에서 얻어온다.
  		.where(...)				// 어떤 Entity에서 데이터를 가져올지 기술
  		.join(...)				// join을 어떤 Entity와 할지 기술
  		.fetch();					// 동적 생성된 SQL 구문 실행(Transaction 또는 영속성 연산 수행)
```

<br>

## 여러가지 fetch

### fetchCount()

count() 만 수행한다.

```java
@Test
public void fetchCount(){
  final QMember member = QMember.member;
  long count = queryFactory.selectFrom(member).fetchCount();
}
```

<br>

### fetchFirst()

가져온 결과의 가장 첫번째 행을 얻는다.

```java
@Test
public void fetchFirst(){
  final QMember member = QMember.member;
  Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
}
```

<br>

### fetchResults()

etchResults() 메서드는 페이징을 포함하는 쿼리를 수행한다.

fetchResults() 메서드의 경우 페이징 쿼리가 복잡해지면,

- 데이터(컨텐츠)
- 카운트

를 가져오는 쿼리의 값이 다를 때가 있다. (성능 때문에..)

성능 문제로 인해 카운트를 가져오는 쿼리를 더 단순하게 만드는 경우가 있다. 복잡하고 성능이 중요한 쿼리에서는  fetchResults()로 한번에 작성하기 보다는 쿼리 두번을 보내는 식으로 작성하는 편이 낫다. 조심하자. 이 부분에 대해서는 6장 쯤에 성능 최적화를 위한 예제를 다루면서 정리를 하고 있다.

```java
@Test
public void fetchResults(){
  final QMember member = QMember.member;
  QueryResults<Member> qResult = queryFactory.selectFrom(member)
    .fetchResults();

  long total = qResult.getTotal();
  List<Member> results = qResult.getResults();

  System.out.println("total cnt 	:: " + total);
  System.out.println("results 	:: " + results);
}
```

<br>

## orderBy() - 정렬

- asc()
  - 오름차순 정렬
- desc()
  - 내림차순 정렬
- nullsLast()
  - 지정한 컬럼의 데이터 값이 null이 아닌 데이터를 우선으로 정렬
- nullsFirst()
  - 지정한 컬럼의 데이터 값이 null인 데이터를 우선으로 정렬

**예제 1) 모든 회원들을 조회하는데 나이순으로 내림차순(desc), 이름순으로 오름차순(asc), 회원 이름이 없을 경우는 마지막에 출력하도록 한다. (nulls last)**

```java
// ... 
	@Test
	public void sort(){
		final QMember member = QMember.member;

		List<Member> sortResult = queryFactory
			.selectFrom(member)
			.where(member.age.goe(100))
			.orderBy(
				member.age.desc(),
				member.username.asc()
					.nullsLast()
			)
			.fetch();

		/** 예상 결과)
		 * 		베토벤 -> 쇼팽 -> 지니 -> null
		 **/
		Member beethoven = sortResult.get(0);
		Member chopin = sortResult.get(1);
		Member genie = sortResult.get(2);
		Member nullEntity = sortResult.get(3);

		assertThat(beethoven.getUsername()).isEqualTo("Beethoven");
		assertThat(chopin.getUsername()).isEqualTo("Chopin");
		assertThat(genie.getUsername()).isEqualTo("Genie");
		assertThat(nullEntity.getUsername()).isNull();
	}
// ...
```



## aggregation - 그루핑, 집합

최소, 최대, 평균, sum 값을 구하는 방법을 알아보자. 최소, 최대, 평균, sum 값을 구하기 위해서는 유일하게 식별할 수  있는 기준컬럼(주로 pk 컬럼)으로 그루핑을 한 후 최소, 최대, 평균, sum을 구한다. 이렇게 집계를 내리기 위해 그루핑을  하는 것을 Aggregation 이라고 부른다.<br>

보통 데이터의 양이 많아질 경우 Web 계층에서 WAS로 조회요청을 할 때 count, min, max, sum을 할때  성능상에 무리가 가는 경우가 많다. 많은 데이터에 대해 count, min, max, sum을 단 한번의 조회요청에 수행하게 될  뿐만 아니라, 여러 사용자가 이런 집계연산을 내리는 경우 DB에 부하가 많이 가게 된다. 이런 이유로 실무에서는 쿼리보다는 테이블 설계로 조금 더 빠르게 수행될 수 있는 아이디어들을 적용하는 것 같다..<br>

(단순히 쿼리만이 능사가 아니다. 테이블 설계시 트릭을 조금만 더 잘 쓰면, 엔티티매핑 이런 것들을 예쁘게 만들 필요도 없다.)<br>

**예1) Member 테이블 내의 사원들의 나이에 대한 max, min, avg, sum 구하기**

```java
List<Tuple> result = queryFactory
  .select(
      member.count(), 
      member.age.max(),
      member.age.min(),
      member.age.avg(),
      member.age.sum()
	)
  .from(member)
  .fetch();
```

<br>

**예2) 회원의 수, 회원들중 최고령자, 최소연령인 회원, 회원들의 평균 나이, 회원들의 나이의 총합을 구해보자**

```java
@Test
public void basicAggregation(){
  QMember member = QMember.member;

  List<Tuple> result = queryFactory
    .select(
    member.count(),
    member.age.max(),
    member.age.min(),
    member.age.avg(),
    member.age.sum()
  ).from(member)
    .fetch();

  int expectedSum = 23+22+28+24+35+41+251+210+210+100;
  Double expectedAvg = expectedSum / 10.000;

  int expectedMin = 22;
  int expectedMax = 251;

  Tuple tuple = result.get(0);
  assertThat(tuple.get(member.age.max())).isEqualTo(expectedMax);
  assertThat(tuple.get(member.age.min())).isEqualTo(expectedMin);
  assertThat(tuple.get(member.count())).isEqualTo(10);
  assertThat(tuple.get(member.age.sum())).isEqualTo(expectedSum);
  assertThat(tuple.get(member.age.avg())).isEqualTo(expectedAvg);
}

```

<br>

**예제 3) 팀의 이름과 각 팀의 평균 연령 구하기**

```java
@Test
public void groupBy() throws Exception{
  QTeam team = QTeam.team;
  QMember member = QMember.member;

  List<Tuple> result = queryFactory
    .select(team.name, member.age.avg())
    .from(member)
    .join(member.team, team)
    .groupBy(team.name)
    .fetch();

  Tuple analysis = result.get(0);
  Tuple marketing = result.get(1);
  Tuple musician = result.get(2);

  for(Tuple t : result){
    System.out.println("t : " + t);
  }

  assertThat(analysis.get(team.name)).isEqualTo("Analysis");
  assertThat(analysis.get(member.age.avg())).isEqualTo(32.0);

  assertThat(marketing.get(team.name)).isEqualTo("Marketing");
  assertThat(marketing.get(member.age.avg())).isEqualTo(22.5);

  assertThat(musician.get(team.name)).isEqualTo("Musician");
  assertThat(musician.get(member.age.avg())).isEqualTo(192.75);
}

```

<br>

## case

> - 가급적이면 case 문에서 이렇게 데이터를 변환하는 것은 권장되는 편은 아니다. 
>   - (강의에서도 어플리케이션에서 변환을 하라고 권고함)
> - 예를 들면 Entity -> Dto 로 반환하는 빌더 구문이 있다고 할 때, Dto로 변환하는 곳에 적용하는 방법이 있을 것 같다.
> - 이 외에도 람다 등을 활용해서 Dto에 변환하는 동작을 다양하게 주입할 수도 있을 것 같다.

<br>

SQL 의 select case 문과 같은 역할을 한다.

```java
@Test
public void select_case_test(){
  List<String> result = queryFactory
    .select(member.age).goe(30).then("아재")
    .select(member.age).loe(29).then("꿈나무")
    .otherwise("보통")
    .from(member)
    .fetch();
}
```

<br>

**CaseBuilder 를 사용하는 예제**<br>

```java
@Test
public void select_case_builder_test(){
  List<String> result = queryFactory
    .select(new CaseBuilder()
            .when(member.age.between(0,20)).then("부럽다 진짜...")
            .when(member.age.between(21,30)).then("일개미에요")
            .otherwise("아재에요"))
    .from(member)
    .fetch();
  
  for(String s : result){
    System.out.println("s = " + s);
  }
}
```

<br>

## 상수, 문자 처리

단순 문자 출력은 `Expressions.constant("A")` 로 수행가능하다.<br>

```java
@Test
public void constant(){
  List<Tuple> result = queryFactory
    .select(member.username, Expressions.constant("님"))
    .from(member)
    .fetch();
  
  for(Tuple tuple : result){
    System.out.println("tuple = " + tuple);
  }
}
```

<br>

concat() 사용해보기<br>

```java
@Test
public void concat(){
  // {username}{님}_{age}
  List<String> result = queryFactory
    .select(member.username.concat("님_").concat(member.age.stringValue()))
    .from(member)
    .where(member.username.eq("지드래곤"))
    .fetch();
  
  for(String s : result){
    System.out.println("s = " + s);
  }
}
```

<br>

## 서브쿼리

서브쿼리를 사용할 때는  `JPAExpressions` 클래스를 사용해서 내부에서 쿼리문을 전달해준다. 서브쿼리에 대한 자세한 내용은 예제로 정리하면 될 것 같다.<br>

### 예제 1) 나이가 가장 많은 사원의 정보를 출력해보기

```java
@Test
@DisplayName("나이가_가장_많은_사람을_출력해보기")
void 나이가_가장_많은_사람을_출력해보기(){
  QEmployee empSub = new QEmployee("employeeSub");

  List<Employee> result = queryFactory.selectFrom(employee)
    .where(employee.age.eq(
      JPAExpressions
      .select(empSub.age.max())
      .from(empSub)
    ))
    .fetch();

  System.out.println(result);

  assertThat(result)
    .extracting("age")
    .containsExactly(41);
}
```

<br>

**SQL 출력결과**

```sql
Hibernate: 
    /* select
        employee 
    from
        Employee employee 
    where
        employee.age = (
            select
                max(employeeSub.age) 
            from
                Employee employeeSub
        ) */ select
            employee0_.employee_id as employee1_1_,
            employee0_.employee_age as employee2_1_,
            employee0_.dept_id as dept_id4_1_,
            employee0_.employee_name as employee3_1_ 
        from
            public.emp employee0_ 
        where
            employee0_.employee_age=(
                select
                    max(employee1_.employee_age) 
                from
                    public.emp employee1_
            )
[Employee(id=7, name=소방관2, age=41, dept=io.study.jpa.querydslv1.company.department.Department@5f5ec9a8)]
```

<br>

### 예제 2) 나이가 전 사원들의 평균 나이 이상인 회원들을 조회하기

```java
@Test
@DisplayName("나이가_평균_이상인_회원")
public void 나이가_평균_이상인_회원(){
  QEmployee empSub = new QEmployee("employeeSub");

  List<Employee> result = queryFactory.selectFrom(employee)
    .where(employee.age.goe(
      JPAExpressions
      .select(empSub.age.avg())
      .from(empSub)
    ))
    .fetch();

  result.forEach(System.out::println);

  assertThat(result)
    .extracting("age")
    .containsExactly(40L,41L);
}
```

<br>

**SQL 생성결과**

```sql
Hibernate: 
    /* select
        employee 
    from
        Employee employee 
    where
        employee.age >= (
            select
                avg(employeeSub.age) 
            from
                Employee employeeSub
        ) */ select
            employee0_.employee_id as employee1_1_,
            employee0_.employee_age as employee2_1_,
            employee0_.dept_id as dept_id4_1_,
            employee0_.employee_name as employee3_1_ 
        from
            public.emp employee0_ 
        where
            employee0_.employee_age>=(
                select
                    avg(cast(employee1_.employee_age as double)) 
                from
                    public.emp employee1_
            )
Employee(id=6, name=소방관1, age=40, dept=io.study.jpa.querydslv1.company.department.Department@37dc8506)
Employee(id=7, name=소방관2, age=41, dept=io.study.jpa.querydslv1.company.department.Department@37dc8506)
```

<br>

### 예제 3) in절 내에서 서브쿼리 사용해보기

```java
@Test
@DisplayName("In절_사용해보기")
public void In절_사용해보기(){
  QEmployee empSub = new QEmployee("employeeSub");

  List<Employee> result = queryFactory.selectFrom(employee)
    .where(employee.age.in(
      JPAExpressions
      .select(empSub.age)
      .from(empSub)
      .where(empSub.age.lt(30L))
    ))
    .fetch();

  result.forEach(System.out::println);

  assertThat(result)
    .extracting("name")
    .containsExactly("황의조","권창훈");
}
```

<br>

SQL 출력결과

```sql
Hibernate: 
    /* select
        employee 
    from
        Employee employee 
    where
        employee.age in (
            select
                employeeSub.age 
            from
                Employee employeeSub 
            where
                employeeSub.age < ?1
        ) */ select
            employee0_.employee_id as employee1_1_,
            employee0_.employee_age as employee2_1_,
            employee0_.dept_id as dept_id4_1_,
            employee0_.employee_name as employee3_1_ 
        from
            public.emp employee0_ 
        where
            employee0_.employee_age in (
                select
                    employee1_.employee_age 
                from
                    public.emp employee1_ 
                where
                    employee1_.employee_age<?
            )
Employee(id=8, name=황의조, age=29, dept=io.study.jpa.querydslv1.company.department.Department@875d0db)
Employee(id=10, name=권창훈, age=27, dept=io.study.jpa.querydslv1.company.department.Department@875d0db)
```

<br>

## static import

지금까지 모든 서브쿼리에서 아래와 같은 구문을 사용했다. 아래와 같은 `JPAExpressions` 를 사용하는 구문은  static import 를 한다면 조금 더 코드를 간결하게 정리할 수 있다.

```java
JPAExpressions
    .select(empSub.age)
    .from(empSub)
    .where(empSub.age.lt(30L))
```



## from 절에서 서브쿼리 미지원

JPA 의  JPQL에서도 from 절에서 서브쿼리를 사용하지 못한다. 이런 이유로 QueryDsl 역시 from 절 내부에 서브쿼리를 사용하지 못한다. from 절에 서브쿼리를 사용하는 것은 보통 인라인 뷰라고 부르는 경우가 많다. 이렇게 인라인 뷰를 사용하는 것은 성능상에 좋지 못하며, 추천되는 방식은 아니다.<br>

이렇게 서브쿼리를 인라인 뷰에 사용하게 되는 인라인 뷰를 사용해야 할 경우 아래와 같은 우회방식들을 고려해볼 수 있다.

- 서브쿼리를  join 으로 변경한다.
- JdbcTemplate 을 사용한다. (정말 좋은 도구!!!이다.)
- 애플리케이션에서 쿼리를 2번 분리해서 실행한다.

<br>

## 참고하면 좋은 책

- SQL Anti Patterns
  - [http://www.yes24.com/Product/Goods/5269099](http://www.yes24.com/Product/Goods/5269099)

<br>

