package io.study.jpa.generation_strategy.company.department;

import io.study.jpa.generation_strategy.company.employee.Employee;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(
    schema = "public", sequenceName = "DEPT_SEQ", name = "department_seq",
    initialValue = 1, allocationSize = 1
)
@Table(name = "DEPT", schema = "public")
public class Department {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_seq")
    @Column(name = "DEPT_ID")
    private Long id;

    @Column(name = "DEPT_NAME")
    private String deptName;

//    @OneToMany(mappedBy = "dept")
//    List<Employee> employees = new ArrayList<>();
}
