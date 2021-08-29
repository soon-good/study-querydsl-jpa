package io.study.jpa.querydslv1.dynamic_query.where;

import static io.study.jpa.querydslv1.company.employee.QEmployee.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
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
