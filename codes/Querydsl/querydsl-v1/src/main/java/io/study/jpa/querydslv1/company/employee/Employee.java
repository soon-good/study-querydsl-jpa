package io.study.jpa.querydslv1.company.employee;

import io.study.jpa.querydslv1.company.department.Department;
import lombok.Builder;
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

    @Column(name = "EMPLOYEE_AGE")
    private Long age;

    @Column(name = "SALARY")
    private Double salary;

    @ManyToOne
    @JoinColumn(name = "DEPT_ID")
    private Department dept;

    public void moveDept(Department dept){
        this.dept = dept;
        dept.getEmployees().add(this);
    }

    public Employee(){}

    public Employee(String name, Department dept, Long age){
        this.name = name;
        this.dept = dept;
        this.age = age;
    }

    @Builder
    public Employee(String name, Department dept, Long age, Double salary){
        this.name = name;
        this.dept = dept;
        this.age = age;
        this.salary = salary;
    }
}
