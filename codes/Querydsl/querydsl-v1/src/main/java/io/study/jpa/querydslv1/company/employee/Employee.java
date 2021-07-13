package io.study.jpa.querydslv1.company.employee;

import io.study.jpa.querydslv1.company.department.Department;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "EMP", schema = "public")
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EMPLOYEE_ID")
    private Long id;

    @Column(name = "EMPLOYEE_NAME")
    private String name;

    @Column(name = "EMPLOYEE_AGE")
    private Long age;

    @ManyToOne
    @JoinColumn(name = "DEPT_ID")
    private Department dept;

    public Employee(){}

    public Employee(String name, Department dept, Long age){
        this.name = name;
        this.dept = dept;
        this.age = age;
    }
}
