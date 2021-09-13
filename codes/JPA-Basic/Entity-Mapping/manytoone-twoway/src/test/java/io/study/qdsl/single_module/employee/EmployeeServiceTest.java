package io.study.qdsl.single_module.employee;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import io.study.qdsl.single_module.company.department.Department;
import io.study.qdsl.single_module.company.department.repository.DepartmentRepository;
import io.study.qdsl.single_module.company.employee.Employee;
import io.study.qdsl.single_module.company.employee.repository.EmployeeRepository;
import io.study.qdsl.single_module.company.employee.service.EmployeeService;

@SpringBootTest
@Transactional
public class EmployeeServiceTest {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private EntityManager em;

	@BeforeEach
	void 사원_부서_데이터_초기화(){
		Department deptSoccer = Department.builder()
			.deptName("축구선수")
			.build();

		Department deptFBManager = Department.builder()
			.deptName("감독")
			.build();

		departmentRepository.save(deptSoccer);
		departmentRepository.save(deptFBManager);

		Employee empSon = Employee.builder()
			.name("손흥민")
			.build();

		// 상대편 객체인 Department 에 Employee 를 추가해줘야 반영된다.
		// 이때 연관관계 편의메서드 사용
		empSon.assignDept(deptSoccer);

		employeeRepository.save(empSon);
		em.flush();
	}

	@Test
	@DisplayName("테스트_Update_SQL_연습__사원_부서이동")
	void 테스트_Update_SQL_연습__사원_부서이동(){
		List<Employee> soccerPlayers = employeeRepository.findEmployeesByNameEquals("손흥민");

		// 변경전 사원 목록 조회.
		// fetch join 으로 데이터를 가지고 왔기 때문에 반대편 객체의 데이터 역시 출력되는 것을 확인할 수 있다.
		// 초기 데이터 INSERT 작업 후 영속성 컨텍스트  commit 을 안한 경우 만약 데이터 INSERT시 연관관계가 객체에는 반영이 안되고 테이블에는 반영된다.
		System.out.println(String.format("부서명 '%s' 의 사원 목록 (변경전) :: %s ", soccerPlayers.get(0).getDept().getDeptName(), soccerPlayers.get(0).getDept().getEmployees()));

		Employee employee = soccerPlayers.get(0);

		List<Department> footballManagers = departmentRepository.findDepartmentByDeptName("감독");
		Department footballManager = footballManagers.get(0);

		employeeService.changeDept(employee.getId(), footballManager.getId());

		assertThat(employee.getDept().getDeptName())
			.isEqualTo("감독");

		System.out.println(String.format("부서명 '%s' 의 사원목록 (변경 후) >>> ", employee.getDept().getDeptName()));
		assertThat(employee.getDept().getEmployees()).hasSize(1);

	}

	@Rollback(value = false)
	@Test
	@DisplayName("테스트_Delete_SQL_연습")
	void 테스트_사원_삭제(){
		List<Employee> soccerPlayers = employeeRepository.findEmployeeByName("손흥민");
		Employee employee = soccerPlayers.get(0);

		employeeService.removeEmployee(employee.getId());

		System.out.println(employee.getDept());
		System.out.println(employee);
	}
}
