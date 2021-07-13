package io.study.jpa.generation_strategy.jpahint;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import io.study.jpa.generation_strategy.company.department.Department;
import io.study.jpa.generation_strategy.company.department.repository.DepartmentRepository;
import io.study.jpa.generation_strategy.company.employee.Employee;
import io.study.jpa.generation_strategy.company.employee.repository.EmployeeRepository;

@SpringBootTest
@Transactional
@Commit
public class JPAHintTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private EmployeeRepository empRepository;

	@Autowired
	private DepartmentRepository deptRepository;

	@BeforeEach
	public void init(){
		Department fireDept = new Department("소방관");
		Department soccerPlayer = new Department("축구선수");
		Department singer = new Department("가수");
		Department developer = new Department("개발자");
		Department studyMan = new Department("혼자공부하는인간");

		em.persist(fireDept);
		em.persist(soccerPlayer);
		em.persist(singer);
		em.persist(developer);
		em.persist(studyMan);

		Employee fire1 = new Employee("소방관1", fireDept);
		Employee fire2 = new Employee("소방관2", fireDept);
		Employee gdragon = new Employee("지드래곤", singer);
		Employee uiJo = new Employee("황의조", soccerPlayer);
		Employee stacey = new Employee("stacey", developer);

		em.persist(fire1);
		em.persist(fire2);
		em.persist(gdragon);
		em.persist(uiJo);
		em.persist(stacey);

		em.flush();
	}

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
}
