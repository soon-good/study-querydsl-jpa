package io.study.qdsl.single_module.cascade;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import io.study.qdsl.single_module.company.department.Department;
import io.study.qdsl.single_module.company.employee.Employee;

@SpringBootTest
@Transactional
public class WithPersistCascadeTest {

	@Autowired
	private EntityManager em;

	@Rollback(value = false)
	@Test
	void 테스트_비영속상태의_객체를_Employee에_지정하더라도_Cascade_PERSIST_라면_저장이된다(){
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
		em.persist(son);em.persist(hwang);
		em.flush();
	}
}