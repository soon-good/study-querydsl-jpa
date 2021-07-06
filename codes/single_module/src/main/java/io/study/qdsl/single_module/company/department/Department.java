package io.study.qdsl.single_module.company.department;

import io.study.qdsl.single_module.company.employee.Employee;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DEPT", schema = "public")
public class Department {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DEPT_ID")
    private Long id;

    @Column(name = "DEPT_NAME")
    private String deptName;

    @OneToMany(mappedBy = "dept")
    List<Employee> employees = new ArrayList<>();
}
