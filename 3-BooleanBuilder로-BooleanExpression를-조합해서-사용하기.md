# BooleanBuilder로 BooleanExpression을 조립해서 사용하기

오늘 정리할 예제는 BooleanBuilder 를 이용해서 여러가지의 BooleanExpression 객체드를 조합해서 제품에 한 특성에 대한 조건식을 메서드화 하는 예제를 살펴볼 예정이다.<br>

맨날 Employee, Department 예제를 다루면서 뭔가 재밌는 예제가 없을까? 하고 생각하던 중에 예제를 뭘로할지 생각났다. 조금은 장난스러운 예제이기는 하지만, 기억에 계속 남기에는 좋은 예제인 것 같다.<br>

오늘 정리할 BooleanBuilder 를 다루기 위해 조회할 직원 객체의 조건은 아래와 같다.<br>

> 나이가 33세 이상이면서 연봉이 9000만원 이상인 직원

위의 조건을 만족하면 잔소리동작(?) 버튼을 동작시키는 조건을 체크하는 조건식을 BooleanBuilder로 만들어보자!!<br>

<br>

## BooleanExpression 을 반환하는 메서드 정의하기

employee 의 name, salary, age 에 대한 조건식을 체크하는 `BooleanExpression` 객체들을 생성하는 메서드는 아래처럼 정의했다. 내용이 쉬워서 세부 내용들을 정리하는 것은 바이트 낭비인 것 같아서 설명은 생략해야 할 것 같다.

```java
public class BooleanBuilderBooleanExpressionTest {

  // ...
  
	public BooleanExpression equalsName(String name){
		return employee.name.eq(name);
	}

	public BooleanExpression greaterThanSalary(Double salary){
		return employee.salary.gt(salary);
	}

	public BooleanExpression greaterThanAge(Long age){
		return employee.age.gt(age);
	}

}
```

<br>

## BooleanBuilder 와 BooleanExpression

BooleanBuilder 는 querydsl의 where절에 파라미터로 전달할 수 있다.<br>

```java
List<Employee> list = queryFactory.selectFrom(employee)
			.where(checkMarriageEmergent(null))
			.fetch();
```

<br>

위의 구문에서 where 절에 BooleanBuilder 객체를 파라미터로 전달하고 있는데, 이 파라미터는  `checkMarriageEmergent(String)` 로 세팅한다. 이 `checkMarriageEmergent(String)` 메서드는 정의는 아래와 같다.<br>

```java
// 잔소리 버튼 작동 버튼
public BooleanBuilder checkMarriageEmergent(String name){
  long targetAge = 33L;
  Double targetSalary = 9000D;

  BooleanBuilder builder = new BooleanBuilder();

  if(StringUtils.hasText(name)){
    builder.and(equalsName(name));
  }

  return builder
    .and(greaterThanAge(targetAge))
    .and(greaterThanSalary(targetSalary));
}
```

- equalsName, greatherThanAge, graterThanSalary 
  - 모두 BooleanExpression 을 반환하는 메서드다.

<br>

**name 파라미터를 where 조건식에 동적으로 바인딩**<br>

위 구문에서 name 이라는 파라미터가 null 이거나 공백일 경우에는 조건식에 추가하지 않도록 아래와 같은 표현식을 사용했다. 이렇게 해서 name 파라미터에 대해서는 아래와 같은 조건식을 동적으로 만들어낼 수 있다. <br>

- 이름이 파라미터로 넘어오면 특정 사람을 지정해서 조회
- 이름이 파라미터로 넘어오지 않으면 모든 사람을 조회

```java
if(StringUtils.hasText(name)){
  builder.and(equalsName(name));
}
```

<br>

**여러가지 조건식을 and 구문을 체이닝해 바인딩하기**<br>

`checkMarriageEmergent(String name)` 메서드에서는 아래와 같이 BooleanBuilder 객체를 리턴하고 있다.

```java
return builder
  .and(greaterThanAge(targetAge))
  .and(greaterThanSalary(targetSalary));
```

<br>

나이가 입력값 보다 크면서, 연봉도 입력 파라미터 값보다 큰 경우에 대한 조건식을 이미 생성되어 있는 `BooleanBuilder` 객체에 덧붙였다.<br>

<br>

이제 이렇게 해서 나이가 33세보다 많으면서 연봉이 9000 보다 크면 부모님께 "결혼좀 하렴" 하고 잔소리를 받을 수있도록 하는 어마무시한 잔소리 ON/OFF 여부 체크 기능 생성되었다...

<br>

## 쿼리에 적용하기

BooleanBuilder 를 반환하는 식을 만들었다면 바로 실제 쿼리에 적용할 수 있다. 예를 들면 아래와 같은 방식이다.

```java
@Test
@DisplayName("잔소리동작_버튼_ON_OFF_체크함수에_걸리는_직원들_조회")
void 잔소리동작_버튼_ON_OFF_체크함수에_걸리는_직원들_조회(){

  List<Employee> list = queryFactory.selectFrom(employee)
    .where(checkMarriageEmergent(null))
    .fetch();

  for(Employee e : list){
    System.out.println("부모님(이구동성) >>> " + e.getName() + ", 올해는 결혼해야하지 않니? ");
  }
}
```

<br>

## 출력결과

```plain
... 
부모님(이구동성) >>> 강동원, 올해는 결혼해야하지 않니? 
부모님(이구동성) >>> 지드래곤, 올해는 결혼해야하지 않니? 
...
```





## 전체예제

```java
package io.study.jpa.querydslv1.dynamic_query.where;

import static io.study.jpa.querydslv1.company.employee.QEmployee.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.study.jpa.querydslv1.company.department.Department;
import io.study.jpa.querydslv1.company.employee.Employee;

@SpringBootTest
@Transactional
public class BooleanBuilderBooleanExpressionTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void init(){
		queryFactory = new JPAQueryFactory(em);

		Department dept_soccer = Department.builder()
			.deptName("축구선수")
			.build();

		Department dept_actor = Department.builder()
			.deptName("영화배우")
			.build();

		Department dept_singer = Department.builder()
			.deptName("가수")
			.build();

		em.persist(dept_soccer); em.persist(dept_actor); em.persist(dept_singer);

		Employee son = Employee.builder()
			.age(28L).name("손흥민").salary(20000D)
			.dept(dept_soccer)
			.build();

		Employee hwang = Employee.builder()
			.age(29L).name("황의조").salary(19999D)
			.dept(dept_soccer)
			.build();

		Employee kang = Employee.builder()
			.age(39L).name("강동원").salary(50000D)
			.dept(dept_soccer)
			.build();

		Employee gd = Employee.builder()
			.age(35L).name("지드래곤").salary(70000D)
			.dept(dept_singer)
			.build();

		em.persist(son); em.persist(hwang); em.persist(kang); em.persist(gd);
	}

	@Test
	@DisplayName("잔소리동작_버튼_ON_OFF_체크함수에_걸리는_직원들_조회")
	void 잔소리동작_버튼_ON_OFF_체크함수에_걸리는_직원들_조회(){

		List<Employee> list = queryFactory.selectFrom(employee)
			.where(checkMarriageEmergent(null))
			.fetch();

		for(Employee e : list){
			System.out.println("부모님(이구동성) >>> " + e.getName() + ", 올해는 결혼해야하지 않니? ");
		}
	}


	// 잔소리 버튼 작동 버튼
	public BooleanBuilder checkMarriageEmergent(String name){
		long targetAge = 33L;
		Double targetSalary = 9000D;

		BooleanBuilder builder = new BooleanBuilder();

		if(StringUtils.hasText(name)){
			builder.and(equalsName(name));
		}

		return builder
			.and(greaterThanAge(targetAge))
			.and(greaterThanSalary(targetSalary));
	}

	public BooleanExpression equalsName(String name){
		return employee.name.eq(name);
	}

	public BooleanExpression greaterThanSalary(Double salary){
		return employee.salary.gt(salary);
	}

	public BooleanExpression greaterThanAge(Long age){
		return employee.age.gt(age);
	}

}
```

<br>

## 엔티티 매핑

엔티티 매핑은 아래와 같이 했다.

### Employee.java

```java
package io.study.jpa.querydslv1.company.employee;

import io.study.jpa.querydslv1.company.department.Department;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "EMP", schema = "public")
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EMPLOYEE_ID")
    private Long id;

    @Column(name = "EMPLOYEE_NAME")
    private String name;

    @Column(name = "EMPLOYEE_AGE")
    private Long age;

    @Column(name = "SALARY")
    private Double salary;

    @ManyToOne
    @JoinColumn(name = "DEPT_ID")
    private Department dept;

    public void moveDept(Department dept){
        this.dept = dept;
        dept.getEmployees().add(this);
    }

    public Employee(){}

    public Employee(String name, Department dept, Long age){
        this.name = name;
        this.dept = dept;
        this.age = age;
    }

    @Builder
    public Employee(String name, Department dept, Long age, Double salary){
        this.name = name;
        this.dept = dept;
        this.age = age;
        this.salary = salary;
    }
}
```

<br>

### Department.java

```java
package io.study.jpa.querydslv1.company.department;

import io.study.jpa.querydslv1.company.employee.Employee;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "DEPT", schema = "public")
public class Department {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DEPT_ID")
    private Long id;

    @Column(name = "DEPT_NAME")
    private String deptName;

    @OneToMany(mappedBy = "dept")
    List<Employee> employees = new ArrayList<>();

    public Department(){}

    @Builder
    public Department(String deptName){
        this.deptName = deptName;
    }
}
```

<br>











