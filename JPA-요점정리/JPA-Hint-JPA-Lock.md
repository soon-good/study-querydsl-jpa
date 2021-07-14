# JPA Hint, JPA Lock



## JPA Hint

SQL 에서 적용되는 Hint가 아니라 JPA 구현체에서 적용되는 Hint 기능이다. Hint는 특정 리포지터리의 쿼리를 엔티티 결과물을 읽기 전용으로만 읽어오려고 하는 경우에 사용한다. 이렇게 가져온 데이터는 읽기 전용이기 때문에 영속성 컨텍스트에 의해 더티체킹이 수행되지 않는다. 성능 최적화를 위해 사용된다고 한다. 강의에서는 영속성 컨텍스트 내에서 더티체킹을 하는데에 드는 비용이 그리 크지 않다는 이야기를 해주고 있다. (하지만, 시계열 데이터, 시간축 기반 기록데이터, iot 데이터는 어마어마 하기때문에 감히 읽기전용을 풀어놓고 쓰기에 겁날 때도 있기는 한것 같다.)<br>

아래와 같은 예제에서 employee를 조회해 온 후에 setName을 통해 이름을 변경하고 나서 flush 를 수행하고 나면, 영속성 컨텍스트(=엔티티 버전관리 저장소)내에 해당 employee 엔티티의 내용과 버전이 변경된다.<br>

```java
@Test
@DisplayName("JPA_힌트테스트1_영속성컨텍스트에_내용이_반영되는_경우")
public void JPA_힌트테스트1_영속성컨텍스트에_내용이_반영되는_경우(){
    em.flush();
    em.clear();

    Employee employee = empRepository
        .findById(1L)
        .orElseGet(()-> new Employee(null, null));

        employee.setName("김두한");

        em.flush();	// employee.setName("김두한") 으로 인해 Update 쿼리 발생
}
```

<br>

실제 SQL이 실행된 결과를 확인해보면 아래와 같다.

```sql
Hibernate: 
    select
        employee0_.employee_id as employee1_1_0_,
        employee0_.dept_id as dept_id3_1_0_,
        employee0_.employee_name as employee2_1_0_ 
    from
        public.emp employee0_ 
    where
        employee0_.employee_id=?
Hibernate: 
    /* update
        io.study.jpa.generation_strategy.company.employee.Employee */ update
            public.emp 
        set
            dept_id=?,
            employee_name=? 
        where
            employee_id=?
```

그리고, 조회 결과를 굳이 영속성 컨텍스트에 담아둘 필요를 못느끼거나, 다른 작업의 쓰기 작업에 일관성을 해치고 싶지 않을 경우가 있다. 또는 더티체킹을 굳이 할 필요가 없는 조회연산이 있을 수도 있다.<br>

이런 경우 단순히 JdbcTemplate으로 쿼리를 수행해도 되고, JPAHint 를 사용해서 읽기 전용으로 엔티티들을 읽어올 수 있다.<br>

<br>

## JPA Hint 사용해보기

엔티티 결과물을 읽어들일때 오로지 읽기 전용으로만 읽어들여야 하는 경우 QueryHint를 사용하면 된다.



### Repository 에 QueryHint 사용 조회 쿼리 추가

먼저 Repository 내의 코드에 QueryHint 를 사용하는 메서드를 추가해주는 내용은 아래와 같다.

```java
package io.study.jpa.generation_strategy.company.employee.repository;
// ... 
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
        // ...
	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	public Optional<Employee> findReadOnlyById(Long id);
}
```

<br>

`@QueryHint(name = "org.hibernate.readOnly", value="true")` 로 설정한 부분은 JPA 내의 properties 중에서 `org.hibernate.readOnly` 를 true 로 세팅하겠다는 뜻이다.<br>

위의 코드를 기반으로 Employee 를 Id로 읽어들인 후에 해당 Entity의 name을 "김두한"으로 수정 후에 flush 하는 코드는 아래와 같다.<br>

```java
@Test
@DisplayName("JPA_힌트테스트2_JPA힌트를_사용해_READONLY로_읽어들여보기")
public void JPA_힌트테스트2_JPA힌트를_사용해_READONLY로_읽어들여보기(){
    em.flush();
    em.clear();

    Employee employee = empRepository
        .findReadOnlyById(1L)
        .orElseGet(()->new Employee(null, null));

    employee.setName("김두한");

    em.flush();	// employee.setName("김두한") 으로 인해 Update 쿼리 발생
}
```

<br>

readOnly 로 걸어두어서 아래와 같이 update 쿼리가 나가지 않는 것을 확인 가능하다.

```sql
Hibernate: 
    /* select
        generatedAlias0 
    from
        Employee as generatedAlias0 
    where
        generatedAlias0.id=:param0 */ select
            employee0_.employee_id as employee1_1_,
            employee0_.dept_id as dept_id3_1_,
            employee0_.employee_name as employee2_1_ 
        from
            public.emp employee0_ 
        where
            employee0_.employee_id=?
```

<br>

## JPA Lock

select for update 와 같은 쿼리를 사용할 수 있도록 JPA에서는 `@Lock(LockModeType.PESSIMISTIC_WRITE)` 을 사용하기도 한다. 이 외에도 LockModeType.OPTIMISITIC_WRITE 등 여러가지의 락 레벨이 있다. 오늘 정리하는 JPA Lock 에서는 예제로 비관적 락을 정리한다. 추후 다양한 Lock 의 종류들을 정리해볼 예정이다.<br>

<br>

### 참고자료 

- 개념설명된 블로그

- - https://dololak.tistory.com/446

- 예제위주로 실제 동작을 테스트해서 정리해주신 분의 블로그

- - https://jinhokwon.github.io/mysql/mysql-select-for-update/

- 자바 ORM 표준 JPA 프로그래밍 - 김영한 님

- - http://www.yes24.com/Product/Goods/19040233

- 정리 필요한 내용들 (낙관적락과 비관적 락 등등 여러가지 개념들)

- - [https://github.com/gosgjung/jpa-study/tree/develop/study/summary/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EA%B3%BC-%EB%9D%BD-%EA%B7%B8%EB%A6%AC%EA%B3%A0-2%EC%B0%A8%EC%BA%90%EC%8B%9C](https://github.com/gosgjung/jpa-study/tree/develop/study/summary/트랜잭션과-락-그리고-2차캐시)
  - [https://github.com/gosgjung/jpa-study/blob/develop/study/summary/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EA%B3%BC-%EB%9D%BD-%EA%B7%B8%EB%A6%AC%EA%B3%A0-2%EC%B0%A8%EC%BA%90%EC%8B%9C/1.3-JPA%EB%9D%BD-%EC%82%AC%EC%9A%A9-JPA%EB%82%99%EA%B4%80%EC%A0%81-%EB%9D%BD-and-JPA%EB%B9%84%EA%B4%80%EC%A0%81-%EB%9D%BD.md](https://github.com/gosgjung/jpa-study/blob/develop/study/summary/트랜잭션과-락-그리고-2차캐시/1.3-JPA락-사용-JPA낙관적-락-and-JPA비관적-락.md)

<br>

### Overview

JPA에는 PESSIMISTIC_LOCK, OPTIMISTIC_LOCK 등의 Lock 들이 있다. 여러가지 레벨의 락 모드 타입이 있다. 이렇게 하는 것은 SQL 에서의 SELECT ~ FOR UPDATE 쿼리와 같은 역할을 수행한다. SELECT ~ FOR UPDATE 쿼리를 수행하는 것은 조회하는 특정 범위의 데이터에 대해 배타적인 Lock 을 거는 방식 중 하나이다.<br>

실시간 트래픽이 많은 서비스에서는 가급적 락을 걸지 않아야 한다. select ~ for update 와 같은 Pessimistic Lock 을 걸면 해당 행에 관련된 정보가 모두 락이 걸린다. 이런 이유로 비관적 락(Pessimistic Lock)보다는 낙관적 락(Optimisitic Lock) 을 활용해서 버저닝과 같은 메커니즘을 활용하는 방식으로 해결하는 방법을 적용하는 것도 권장되는 편이다.<br>

실시간 트래픽, 조회를 처리하는 것이 아닌 돈을 맞춰본다던가 하는 작업을 하는 경우는 DB에서 제공하는 Pessimistic Lock 을 이용해 select for ~ update 구문을 활용하는 것이 나쁜 선택은 아니다.<br>

### Select ~ for Update 쿼리 

SELECT ~ FOR UPDATE 구문은 "특정 로우를 SELECT 하는 동안 누구든지 다른 세션에서 이 부분은 무조건 못건들게 Lock" 하는 쿼리이다.

자세한 내용은 https://dololak.tistory.com/446 을 참고하면 될 것 같다.

<br>

### 테스트

**EmployeeRepository.java**<br>

LockModeType 을 통해서 비관적 락을 거는 쿼리를 Repository 내에 아래와 같이 추가해주었다.

```java
package io.study.jpa.generation_strategy.company.employee.repository;

// ...

import org.springframework.data.jpa.repository.Lock;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

        // ...

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public Optional<Employee> findLockById(Long id);
}
```

<br>

**테스트 코드**

```java
@Test
@DisplayName("JPA_LockMode_PESSIMISTIC_WRITE_로_데이터_읽어들이기")
public void JPA_LockMode_PESSIMISTIC_WRITE_로_데이터_읽어들이기(){
    em.flush();
    em.clear();

    Employee employee = empRepository
        .findLockById(1L)
        .orElseGet(()->new Employee(null, null));

}
```

<br>

**실제 쿼리 생성 결과**<br>

아래 SQL 코드 처럼 Select For Update 구문이 실행된 것을 확인 가능하다.

```sql
Hibernate: 
    /* select
        generatedAlias0 
    from
        Employee as generatedAlias0 
    where
        generatedAlias0.id=:param0 */ select
            employee0_.employee_id as employee1_1_,
            employee0_.dept_id as dept_id3_1_,
            employee0_.employee_name as employee2_1_ 
        from
            public.emp employee0_ 
        where
            employee0_.employee_id=? for update
                of employee0_
```



## Lock 의 다양한 종류

락의 다양한 종류에 대해서는 예전에 정리해둔 적이 있었다. 하지만, 그때는 정확히 안다기 보다는 공부를 위해서 정리했던 것 같다. Lock 모드에 대해 정리할 때 한번 더 깔끔하게 정리해볼까 생각중이다. <br>

낙관적 락과 비관적 락을 어떤 때에 사용하는지 등을 잘 알고 있는다면, 실제 트래픽이 존재하는 시스템 내에서 수정/삭제/insert 가 일어날 때에 데이터의 정합성을 맞추기 위한 나름의 규칙을 어떻게 적용할지, 잘 정할수 있는 나름의 기준이 생길것 같다.<br>

<br>

[https://github.com/gosgjung/jpa-study/tree/develop/study/summary/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EA%B3%BC-%EB%9D%BD-%EA%B7%B8%EB%A6%AC%EA%B3%A0-2%EC%B0%A8%EC%BA%90%EC%8B%9C](https://github.com/gosgjung/jpa-study/tree/develop/study/summary/트랜잭션과-락-그리고-2차캐시)

