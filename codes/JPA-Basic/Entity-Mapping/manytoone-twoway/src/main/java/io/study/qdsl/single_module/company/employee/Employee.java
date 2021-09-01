package io.study.qdsl.single_module.company.employee;

import io.study.qdsl.single_module.company.department.Department;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "EMP", schema = "public")
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EMPLOYEE_ID")
    private Long id;

    @Column(name = "EMPLOYEE_NAME")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "DEPT_ID")
    private Department dept;

    public Employee(){}

    @Builder
    public Employee(Long id, String name, Department department){
        this.id = id;
        this.name = name;
        this.dept = department;
    }

    public void setDept(Department dept){
        this.dept = dept;
    }

    public void assignDept(Department dept){
        this.dept = dept;
        dept.getEmployees().add(this);
    }
}
