# QueryDsl 의 프로젝션

프로젝션이라는 것은 QueryDsl 에서는 결과값을 어떻게 받아낼지를 의미한다. 역시 말로만 설명해서는 뜬구름잡는 이야기이다. 아래 예를 살펴보자.

```java
QMember member = QMember.member;
List<String> result = queryFactory
  .select(member.username)
  .from(member)
  .fetch();
```

사원 명 `username` 만을 리스트로 출력하는 쿼리이다.<br>

<br>

만약, 두 개 이상의 컬럼을 Entity 의 각 필드에 상관 없이, 원하는 컬럼만 추려서 받아야 하는 경우는 어떻게 해야 할까? 예를 들면 어떤 테이블은 컬럼이 40개나 존재하는데, 여기서 필요한 컬럼들만 Dto로 바인딩해서 조회하려고 하는 경우도 있을 수 있다. (물론 이런 경우가 있는지는 나도 잘 모르겠다.) 이럴 때에 프로젝션이라는 개념을 사용하게 된다.<br>

<br>

## 프로젝션에서의 자료형

보통 프로젝션을 한다고 할때 고려되는 자료형은 3가지이다.<br>

- String, Long, ... 등의 기본 래퍼(Wrapper) 클래스
- 튜플
  - QueryDsl 에서 제공해주는 QueryDsl 특화자료형이다. 자주 사용되는 자료형은 아니다.
  - QueryDsl 라이브러리에 의존적이기 때문에 권장되는 자료형은 아니다.
- Dto
  - 데이터의 반환형을 직접 정의해 반환하는 방식
  - 프리젠테이션 계층에 반환할 객체를 따로 정의하는 방식이다.
  - Entity와 같은 형식의 데이터는 도메인에 가깝기에 최대한 훼손시키지 않으면서, 화면 로직의 잦은 유지보수, 변경사항에 대응해 Dto로 바인딩하는 것은 어느 정도는 나름 대로 권장되는 방식이다.

<br>

## 프로젝션 1) 기본 Wrapper 자료형으로 프로젝션

ex 1) 단순 String의 리스트를 출력해보기

```java
QMember member = QMember.member;
List<String> result = queryFactory
      .select(member.username)
      .from(member)
      .fetch();
```

<br>

## 프로젝션 2) 튜플을 사용하는 방식

- 튜플은 정말 왠만하면 사용하지 않는다.

```java
import com.querydsl.core.Tuple;
// ...
// ...
QMember member = QMember.member;
List<Tuple> result = queryFactory
  		.select(member)
  		.from(member)
  		.fetch();
```

출력결과

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch04/img/DEBUG_TUPLE_INSPECTION.png)

위의 데이터에서 리스트의 첫번째 요소의 사용자 명을 가져와보자.

![이미지](https://raw.githubusercontent.com/gosgjung/study_archives/master/java/queryDsl/ch04/img/DEBUG_TUPLE_EXPRESSION1.png)

<br>

## 프로젝션 3) Dto 프로젝션 (기본)

주의) 주의할 점은 Dto 에 기본생성자를 반드시 선언해주어야 한다는 점이다. (querydsl 입장에서는 메모리에 해당 Dto의 인스턴스를 만들어놓고 필드들을 주입해야 하는데, 기본생성자가 없으면 메모리 공간을 만들 수 없기 때문이다. 이런 이유로 반드시 Dto 를 querydsl 에서 프로젝션하려고 할 경우에는 기본생성자를 선언해주어야 한다.)<br>

<br>

- 프로퍼티에 접근해서 Projection 하는 방식
  - ex) `Projections.bean(MemberDto.class, member.username, member.age)`
- 필드에 직접 접근해서 Projection 하는 방식
  - ex) `Projections.fields(MemberDto.class, member.username, member.age)`
- 생성자를 이용해 Projection 하는 방식
  - ex) `Proejctions.constructor(MemberDto.class, member.username, member.age)`

<br>

흔히, 의존성 주입방식이라고 해서, 프로퍼티 주입, 필드 주입, 생성자 주입 이렇게 이야기 하는데, 여기에서도 querydsl의 프로젝션 방식에서도 비슷한 방식으로 프로퍼티 주입, 필드 주입, 생성자 주입 등의 방식을 사용한다. 신기하다!!!<br>

<br>

## Dto 프로젝션 (1) - Projections.bean 사용방식

- **querydsl의 Projections.bean(...)을 사용하는 방식이다.**
- 자바 빈 규약 (Getter/Setter)를 활용한 방식이다.
- 반드시 DTO내에 기본 생성자(또는 롬복의 @NoArgsConstructor) 가 선언되어 있어야 동작한다.

ex)

```java
@Test
public void dtoProjectionBySetter(){
  QMember member = QMember.member;

  List<MemberDto> dtoList = queryFactory
    .select(
    Projections.bean(
      MemberDto.class,
      member.username,
      member.age
    )
  )
    .from(member)
    .fetch();

  for(MemberDto d : dtoList){
    System.out.println("data :: " + d);
  }
}
```

<br>

## Dto 프로젝션 (2) - Projections.fields 사용방식

- **querydsl의 Projections.fields(...) 메서드를 사용하는 방식이다.**
- DTO의 필드에 직접 접근하는 방식이다.
- DTO 멤버 필드의 private 멤버필드도 리플렉션을 쓰면 가져올 수 있다.
   즉, 내부적으로 리플렉션이 적용되어 있다.
- 반드시 DTO 클래스 내에 기본생성자(또는 @NoArgsConstructor)가 있어야 동작한다.

<br>

```java
@Test
public void dtoProjectionByField(){
  QMember member = QMember.member;

  List<MemberDto> dtoList = queryFactory
    .select(
    Projections.fields(
      MemberDto.class,
      member.username,
      member.age
    )
  )
    .from(member)
    .fetch();

  for(MemberDto d : dtoList){
    System.out.println("data :: " + d);
  }
}
```

<br>

## Dto 프로젝션 (3) - Proejctions.constructor 사용방식

- **Projections.constructor(...) 메서드를 사용하는 방식이다.**
- 반드시 DTO클래스 내에 기본 생성자(또는 @NoArgsConstructor)가 있어야 동작한다.

```java
@Test
public void dtoProjectionByConstructor(){
  QMember member = QMember.member;

  List<MemberDto> dtoList = queryFactory
    .select(
    Projections.constructor(
      MemberDto.class,
      member.username,
      member.age
    )
  )
    .from(member)
    .fetch();

  for(MemberDto d : dtoList){
    System.out.println("data :: " + d);
  }
}
```

<br>

## Dto 프로젝션 (3.1) - 컬럼명이 Dto의 필드와 일치하지 않을 경우

- Dto 바인딩시 필드명을 다르게 해서 화면에 전달해주고 싶은 경우가 있다.
- 필드명이 다른 Dto로 쿼리 결과를 가져올 때 JPA에서 쿼리 내에 지정한 Dto 에 이름이 맞는 필드가 없을 경우
   null 로 값을 주입하여 반환해준다.

MemberDto와 필드명이 다른 UserDto의 예를 들어보자.<br>

(UserDto는 username 필드 대신 name 필드가 있다.)<br>

**예1) DTO 클래스 내에 이름이 같은 필드명이 없어서 null 값이 주입되는 예**<br>

```java
@Test
public void dtoProjectionAliasBasic(){
  QMember member = QMember.member;

  List<UserDto> dtoList = queryFactory
    .select(
    Projections.fields(
      UserDto.class,
      member.username,
      member.age
    )
  )
    .from(member)
    .fetch();

  /** 결과를 확인해보면 에러는 나지 않는데, name 필드에 null 값이 들어간다. */
  System.out.println("===== Non Alias Result =====");
  for(UserDto d : dtoList){
    System.out.println("data :: " + d);
  }
}
```

<br>

**예2) DTO 클래스 내에 이름이 같은 필드명이 없는 경우 As를 주어 프로젝션 하는 예**

```java
@Test
public void dtoProjectionAliasBasic(){
  QMember member = QMember.member;

  /** ex) member.username.as("name") */
  List<UserDto> aliasResult = queryFactory.select(
    Projections.fields(
      UserDto.class,
      member.username.as("name"),		// ExpressionUtils.as(member.username, "as") 와 같은 표현이다.
      member.age
    )
  ).from(member).fetch();

  /** 결과를 확인해보면 제대로 값이 들어와 있다.. */
  System.out.println("===== Alias Result =====");
  for(UserDto d : dtoList){
    System.out.println("data :: " + d);
  }
}
```

- Member클래스의 username 필드의 값들이 UserDto 클래스의 `name` 필드로 매핑되도록 alias를 주었다.

<br>

## Dto 프로젝션 (4) - 서브쿼리 프로젝션

서브쿼리 사용시 `ExpressionUtils.as(JPAExpressions.select(...).from(...), "별칭")` 과 같은 방식으로 사용한다.<br>

```java
@Test
public void dtoProjectionAliasSubquery(){
  QMember member = QMember.member;
  QMember subMember = new QMember("subMember");

  List<UserDto> aliasResult = queryFactory.select(
    Projections.fields(
      UserDto.class,
      member.username.as("name"),

      ExpressionUtils.as(
        JPAExpressions.select(
          subMember.age.max()
        )
        .from(subMember),
        "age"
      )
    )
  ).from(member).fetch();

  System.out.println("===== Alias Result =====");
  for(UserDto d : aliasResult){
    System.out.println("data :: " + d);
  }
}

```

<br>

서브쿼리를 사용하기 위해서는 별도의 QType 인스턴스를 하나 더 생성해야 한다.<br>

```java
QMember subMember = new QMember("subMember");
// ...
// ...
queryFactory.select(
  Projections.fields(
    UserDto.class, member.username.as("name"),
    ExpressionUtils.as(		// 이 안에서 subQuery용 QType 인스턴스를 사용
    		JPAExpressions
      					.select( subMember.age.max() )
    						.from( subMember ),
    		"age"
  	)
  )
).from(member).fetch();
```

> 원리적으로 이해해보면...(=라고 쓰고 감으로 때려맞혀보면... 이라고 말한다.) <br>
>
> 이미 쿼리를 돌리고 있는 QType 인스턴스 A가 있는데 인스턴스 A 안에서 또 쿼리 수행을 할 수는  없으므로 별도로 결과를 뽑아올 수 있는 QType 인스턴스 B를 새로 생성해 표현식으로 넘겨주는 방식이다.

<br>

## 참고) JPQL 에서의 Dto 프로젝션

그냥 참고용도로 남겨보았다~!!

```java
@Test
public void dtoProjectionByJPQL(){
  List<MemberDto> resultList = em
    .createQuery(
    "select new com.study.qdsl.dto.MemberDto(m.username, m.age) from Member m",
    MemberDto.class
  )
    .getResultList();

  for(MemberDto d : resultList){
    System.out.println("memberDto :: " + d);
  }
}

```

<br>

## Dto 프로젝션 (5) - @QueryProjection 

`@QueryProjection` 을 사용하면 Projection 으로 지정할 Dto 에 잘못된 값을 문법적인 레벨(=컴파일 레벨)에서 파악할 수 있다는 장점이 있다. (프로그램을 Run 시키는 Runtime 이전에 컴파일 레벨에서 오류를 파악할 수 있기에 안전하다는 장점이 있는 것 같다.)<br>

<br>

**단점**<br>

QueryProjection 을 사용할 경우 애플리케이션이 전반적으로 QueryDsl 라이브러리에 의존적인 코드가 된다.<br>

Querydsl에 대한 확신이 있다면, QueryProjection을 사용하는 것도 나쁘지 않은 방식이다.<br>

<br>

**내 생각**<br>

프리젠테이션 레벨에서만 사용된다면, QueryDsl 로직이 아닌, Dto에 어노테이션 하나를 추가하는 것은 나쁘지 않은 선택인것 같다. SQL 로직, 즉 도메인/엔티티 로직에  `Projections.constructor()`  과 같은 메서드를 사용할 경우는 모델 계층의 로직을 변경해야 하기에 차라리 Dto 레벨에서 `@QueryProjection` 어노테이션을 적용하는 것이 더 나쁘지 않은 방식인 것 같다는 생각이 든다.<br>

<br>

**MemberDto.java**

```java
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
// ...
// ...
@Data 
@NoArgsConstructor
public class MemberDto {

	private String username;
	private int age;

  // 여기에 추가해주었다.
	@QueryProjection
	public MemberDto(String username, int age){
		this.username = username;
		this.age = age;
	}
}
```

<br>

위 소스를 보면 생성자 위에 `@QueryProjection` 어노테이션을 붙여주었다.<br>

**테스트 해보기**<br>

```java
@Test
public void dtoProjectionByQueryProjection(){
  List<MemberDto> data = queryFactory.select(
    new QMemberDto(member.username, member.age)
  )
    .from(member)
    .fetch();

  for(MemberDto m : data){
    System.out.println("memberDto :: " + m);
  }
}
```

<br>

