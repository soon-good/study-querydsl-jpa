package io.study.jpa.querydslv1;

import static io.study.jpa.querydslv1.company.employee.QEmployee.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.study.jpa.querydslv1.company.department.Department;
import io.study.jpa.querydslv1.company.department.repository.DepartmentRepository;
import io.study.jpa.querydslv1.company.employee.Employee;
import io.study.jpa.querydslv1.company.employee.QEmployee;
import io.study.jpa.querydslv1.company.employee.repository.EmployeeRepository;

@SpringBootTest
@Transactional
@Commit
public class SubqueryTest {

	@Autowired
	private EntityManager em;

	private JPAQueryFactory queryFactory;

	@Autowired
	private EmployeeRepository empRepository;

	@Autowired
	private DepartmentRepository deptRepository;

	@BeforeEach
	public void init(){
		queryFactory = new JPAQueryFactory(em);

		Department deptFireman = new Department("소방관");
		Department deptSinger = new Department("가수");
		Department deptSoccerPlayer = new Department("축구선수");

		deptRepository.save(deptFireman);
		deptRepository.save(deptSinger);
		deptRepository.save(deptSoccerPlayer);

		empRepository.save(new Employee("지드래곤", deptSinger, 31L));
		empRepository.save(new Employee("태양", deptSinger, 32L));
		empRepository.save(new Employee("소방관1", deptFireman, 40L));
		empRepository.save(new Employee("소방관2", deptFireman, 41L));
		empRepository.save(new Employee("황의조", deptSoccerPlayer, 29L));
		empRepository.save(new Employee("손흥민", deptSoccerPlayer, 33L));
		empRepository.save(new Employee("권창훈", deptSoccerPlayer, 27L));

		em.flush();
	}

	@Test
	@DisplayName("나이가_가장_많은_사람을_출력해보기")
	void 나이가_가장_많은_사람을_출력해보기(){
		QEmployee empSub = new QEmployee("employeeSub");

		List<Employee> result = queryFactory.selectFrom(employee)
			.where(employee.age.eq(
				JPAExpressions
					.select(empSub.age.max())
					.from(empSub)
			))
			.fetch();

		System.out.println(result);

		assertThat(result)
			.extracting("age")
			.containsExactly(41L);
	}

	@Test
	@DisplayName("나이가_평균_이상인_회원")
	public void 나이가_평균_이상인_회원(){
		QEmployee empSub = new QEmployee("employeeSub");

		List<Employee> result = queryFactory.selectFrom(employee)
			.where(employee.age.goe(
				JPAExpressions
					.select(empSub.age.avg())
					.from(empSub)
			))
			.fetch();

		result.forEach(System.out::println);

		assertThat(result)
			.extracting("age")
			.containsExactly(40L,41L);
	}

	@Test
	@DisplayName("In절_사용해보기")
	public void In절_사용해보기(){
		QEmployee empSub = new QEmployee("employeeSub");

		List<Employee> result = queryFactory.selectFrom(employee)
			.where(employee.age.in(
				JPAExpressions
					.select(empSub.age)
					.from(empSub)
					.where(empSub.age.lt(30L))
			))
			.fetch();

		result.forEach(System.out::println);

		assertThat(result)
			.extracting("name")
			.containsExactly("황의조","권창훈");
	}

	@Test
	@DisplayName("SELECT절에_Subquery_사용해보기")
	public void SELECT절에_Subquery_사용해보기(){

	}
}
