package io.study.jpa.generation_strategy.sequence_test;

import io.study.jpa.generation_strategy.company.department.Department;
import io.study.jpa.generation_strategy.company.department.repository.DepartmentRepository;
import io.study.jpa.generation_strategy.company.employee.Employee;
import io.study.jpa.generation_strategy.company.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
@Commit
public class SequenceGeneratorTest {

    @Autowired
    EntityManager em;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Test
    @DisplayName("비어있는_테스트")
    void 비어있는_테스트(){

    }

    @Test
    @DisplayName("데이터_하나_추가해보기_테스트")
    void 데이터_하나_추가해보기_테스트(){
        Department deptSinger = new Department("가수");
        departmentRepository.save(deptSinger);
        em.flush();

        Department deptSoccerPlayer = new Department("축구선수");
        departmentRepository.save(deptSoccerPlayer);
        em.flush();

        Employee singer1 = new Employee("지드래곤", deptSinger);
        employeeRepository.save(singer1);
        em.flush();

        Employee soccerPlayer1 = new Employee("황의조", deptSoccerPlayer);
        employeeRepository.save(soccerPlayer1);
        em.flush();
    }
}
