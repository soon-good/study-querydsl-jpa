package io.study.qdsl.single_module;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import io.study.qdsl.single_module.company.department.Department;
import io.study.qdsl.single_module.company.employee.Employee;

@SpringBootTest
@Transactional
public class ManyToOneTwoWayTest {

	@Autowired
	EntityManager em;

	@BeforeEach
	void init(){
	}

	@Rollback(false)
	@Test
	@DisplayName("Department에만_Employee를_추가했을_경우")
	void Department에만_Employee를_추가했을_경우(){
		Department deptSoccer = Department.builder().deptName("축구선수").build();
		Department deptActor = Department.builder().deptName("영화배우").build();

		Employee son = Employee.builder()
			.name("손흥민")
			// .department(deptSoccer)
			.build();

		Employee hwang = Employee.builder()
			.name("황의조")
			// .department(deptSoccer)
			.build();

		Employee ju = Employee.builder()
			.name("주성치")
			// .department(deptActor)
			.build();

		Employee kang = Employee.builder()
			.name("강동원")
			// .department(deptActor)
			.build();

		deptSoccer.getEmployees().add(son);
		deptSoccer.getEmployees().add(hwang);
		deptActor.getEmployees().add(ju);
		deptActor.getEmployees().add(kang);

		em.persist(deptSoccer);
		em.persist(deptActor);
		em.persist(son);em.persist(hwang);em.persist(ju);em.persist(kang);
		em.flush();
	}

	@Rollback(false)
	@Test
	@DisplayName("Employee에만_Department를_추가했을_경우")
	void Employee에만_Department를_추가했을_경우(){
		Department deptSoccer = Department.builder().deptName("축구선수").build();
		Department deptActor = Department.builder().deptName("영화배우").build();

		em.persist(deptSoccer); em.persist(deptActor);

		Employee son = Employee.builder()
			.name("손흥민")
			.department(deptSoccer)
			.build();

		Employee hwang = Employee.builder()
			.name("황의조")
			.department(deptSoccer)
			.build();

		Employee ju = Employee.builder()
			.name("주성치")
			.department(deptActor)
			.build();

		Employee kang = Employee.builder()
			.name("강동원")
			.department(deptActor)
			.build();

		// deptSoccer.getEmployees().add(son);
		// deptSoccer.getEmployees().add(hwang);
		// deptActor.getEmployees().add(ju);
		// deptActor.getEmployees().add(kang);

		em.persist(deptSoccer);
		em.persist(deptActor);
		em.persist(son);em.persist(hwang);em.persist(ju);em.persist(kang);
		em.flush();

		System.out.println("영화배우 직원들의 수 :: " + deptActor.getEmployees().size() + " 명");
		System.out.println("축구선수 직원들의 수 :: " + deptSoccer.getEmployees().size() + " 명");
	}

	@Rollback(false)
	@Test
	@DisplayName("연관관계_편의메서드를_사용하는_경우")
	void 연관관계_편의메서드를_사용하는_경우(){
		Department deptSoccer = Department.builder().deptName("축구선수").build();
		Department deptActor = Department.builder().deptName("영화배우").build();

		em.persist(deptSoccer); em.persist(deptActor);

		Employee son = Employee.builder()
			.name("손흥민")
			.department(deptSoccer)
			.build();
		son.assignDept(deptSoccer);

		Employee hwang = Employee.builder()
			.name("황의조")
			.department(deptSoccer)
			.build();
		hwang.assignDept(deptSoccer);

		Employee ju = Employee.builder()
			.name("주성치")
			.department(deptActor)
			.build();
		ju.assignDept(deptActor);

		Employee kang = Employee.builder()
			.name("강동원")
			.department(deptActor)
			.build();
		kang.assignDept(deptActor);

		em.persist(deptSoccer);
		em.persist(deptActor);
		em.persist(son);em.persist(hwang);em.persist(ju);em.persist(kang);
		em.flush();

		System.out.println("영화배우 직원들의 수 :: " + deptActor.getEmployees().size() + " 명");
		System.out.println("축구선수 직원들의 수 :: " + deptSoccer.getEmployees().size() + " 명");
	}
}
