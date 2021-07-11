package io.study.jpa.generation_strategy.nplus1;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
public class NPlus1Test {

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
	@DisplayName("N_Plus_1_조회테스트")
	public void N_Plus_1_조회테스트(){
		em.clear();
		List<Employee> list = empRepository.findAll();

		for(Employee e : list){
			String deptName = e.getDept().getDeptName();
			System.out.println(e.getName() + "의 소속부서 : " + deptName);
		}
	}

	@Test
	@DisplayName("일반조인을_활용한_조회테스트")
	public void 일반조인을_활용한_조회테스트(){
		em.clear();
		String joinJpql = "select e from Employee e join e.dept d";
		TypedQuery<Employee> joinQuery = em.createQuery(joinJpql, Employee.class);
		List<Employee> joinList = joinQuery.getResultList();

		for(Employee e : joinList){
			System.out.println(e.getName());
		}
	}

	@Test
	@DisplayName("일반_INNER_조인을_활용한_조회테스트")
	public void 일반_INNER_조인을_활용한_조회테스트(){
		em.clear();
		String innerJoinJpql = "select e from Employee e inner join e.dept d";
		TypedQuery<Employee> innerJoinQuery = em.createQuery(innerJoinJpql, Employee.class);
		List<Employee> innerJoinList = innerJoinQuery.getResultList();

		for(Employee e : innerJoinList){
			System.out.println(e.getName());
		}
	}

	@Test
	@DisplayName("일반_INNER_조인_조건식_조회테스트_1")
	public void 일반_INNER_조인_조건식_조회테스트_1(){
		em.clear();
		String innerJoinWhereJpql = "select e from Employee e join e.dept d where e.dept.id = 1";
		TypedQuery<Employee> innerJoinWhereQuery = em.createQuery(innerJoinWhereJpql, Employee.class);
		List<Employee> innerJoinWhereList = innerJoinWhereQuery.getResultList();

		for(Employee e : innerJoinWhereList){
			System.out.println(e.getName() + " 의 부서 : " + e.getDept().getDeptName());
		}
	}

	@Test
	@DisplayName("일반_INNER_조인_조건식_조회테스트_2")
	public void 일반_INNER_조인_조건식_조회테스트_2(){
		em.clear();
		String innerJoinWhereJpql = "select e from Employee e join e.dept d where d.id = 1";
		TypedQuery<Employee> innerJoinWhereQuery = em.createQuery(innerJoinWhereJpql, Employee.class);
		List<Employee> innerJoinWhereList = innerJoinWhereQuery.getResultList();

		for(Employee e : innerJoinWhereList){
			System.out.println(e.getName() + " 의 부서 : " + e.getDept().getDeptName());
		}
	}

	@Test
	@DisplayName("페치조인을_활용한_조회테스트")
	public void 페치조인을_활용한_조회테스트(){
		em.clear();
		String joinFetchJpql = "select e from Employee e join fetch e.dept d";
		TypedQuery<Employee> joinFetchQuery = em.createQuery(joinFetchJpql, Employee.class);
		List<Employee> joinFetchResultList = joinFetchQuery.getResultList();

		for(Employee e : joinFetchResultList){
			System.out.println(e.getName() + " 의 부서 : " + e.getDept().getDeptName());
		}
	}

	@Test
	@DisplayName("EntityGraph를_활용한_조회테스트")
	public void EntityGraph를_활용한_조회테스트(){
		em.clear();
		List<Employee> employeeList = empRepository.findAllUsingEntityGraph();
		for(Employee e : employeeList){
			System.out.println(e.getName() + " 의 부서 : " + e.getDept().getDeptName());
		}
	}
}
