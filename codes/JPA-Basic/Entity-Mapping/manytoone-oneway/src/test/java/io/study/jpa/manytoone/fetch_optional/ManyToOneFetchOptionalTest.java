package io.study.jpa.manytoone.fetch_optional;

import io.study.jpa.manytoone.company.department.Department;
import io.study.jpa.manytoone.company.department.repository.DepartmentRepository;
import io.study.jpa.manytoone.company.employee.Employee;
import io.study.jpa.manytoone.company.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Transactional
@SpringBootTest
class ManyToOneFetchOptionalTest {

    @Autowired
    EntityManager em;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    public void init(){
        Department deptSoccer = new Department("축구선수");
        Department deptSinger = new Department("가수");

        em.persist(deptSoccer);
        em.persist(deptSinger);

        Employee e1 = new Employee("황의조", deptSoccer);
        Employee e2 = new Employee("손흥민", deptSoccer);
        Employee e3 = new Employee("권창훈", deptSoccer);
        Employee e4 = new Employee("지드래곤", deptSinger);
        Employee e5 = new Employee("블랙핑크", deptSinger);
        em.persist(e1);
        em.persist(e2);
        em.persist(e3);
        em.persist(e4);
        em.persist(e5);
    }

    @Test
    @DisplayName("optional_false_테스트_단건조회")
    public void optional_false_테스트_단건조회(){
        em.flush();
        em.clear();
        Employee e1 = employeeRepository.findById(3L).orElseGet(()->{
            return new Employee();
        });

        System.out.println("e1 >>> " + e1.toString());
    }

    @Test
    @DisplayName("optional_false_테스트_리스트조회")
    public void optional_false_테스트_리스트조회(){
        em.flush();
        em.clear();

        List<Employee> list = employeeRepository.findAll();

//        for(Employee e : list){
//            System.out.println("e >>> " + e.toString());
//        }
    }
}
