package io.study.jpa.entitycache;

import io.study.jpa.entitycache.company.employee.Employee;
import io.study.jpa.entitycache.company.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DatasourceTest {

    @Autowired
    private EmployeeRepository repository;

    @Test
    void simpleFindAll() {
        List<Employee> list = repository.findAll();
        System.out.println(list);
    }
}
