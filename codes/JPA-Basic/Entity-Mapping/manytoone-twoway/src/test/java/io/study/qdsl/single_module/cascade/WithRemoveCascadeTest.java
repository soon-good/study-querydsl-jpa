package io.study.qdsl.single_module.cascade;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import io.study.qdsl.single_module.company.department.Department;
import io.study.qdsl.single_module.company.employee.Employee;

@Disabled
@Transactional
@SpringBootTest
public class WithRemoveCascadeTest {

	@Autowired
	private EntityManager em;

	@Disabled
	@Rollback(value = false)
	@Test
	void 테스트_Department_객체_삭제시_CascadeType_REMOVE를_지정하면_연관객객체인_Employee들도_모두_삭제되어야한다(){
		Department deptSoccer = Department.builder().deptName("축구선수").build();

		Employee son = Employee.builder()
			.name("손흥민")
			.department(deptSoccer)
			.build();

		Employee hwang = Employee.builder()
			.name("황의조")
			.department(deptSoccer)
			.build();

		deptSoccer.getEmployees().add(son);
		deptSoccer.getEmployees().add(hwang);

		// em.persist(deptSoccer);
		em.persist(son);
		em.persist(hwang);
		em.flush();


		em.remove(deptSoccer);
		System.out.println("employees >>> " + deptSoccer.getEmployees());
	}
}
