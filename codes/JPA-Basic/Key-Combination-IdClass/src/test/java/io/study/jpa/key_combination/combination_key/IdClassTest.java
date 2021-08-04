package io.study.jpa.key_combination.combination_key;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import io.study.jpa.key_combination.company.department.Department;
import io.study.jpa.key_combination.company.employee.Employee;
import io.study.jpa.key_combination.company.employee.EmployeeId;

@SpringBootTest
@Transactional
@Commit
public class IdClassTest {

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("식별자_테스트테스트")
	public void 식별자_테스트테스트() throws Exception{
		Employee employee = new Employee();
		employee.setEmail("helloworld@naver.com");
		employee.setName("안뇽");

		Department dept = new Department("소방서");
		employee.setDept(dept);

		em.persist(dept);
		em.persist(employee);

		EmployeeId empId = EmployeeId.builder().id(1L).email("helloworld@naver.com").build();
		em.flush();

		em.clear();
		Employee employee1 = em.find(Employee.class, empId);
		System.out.println(employee1);
	}
}
