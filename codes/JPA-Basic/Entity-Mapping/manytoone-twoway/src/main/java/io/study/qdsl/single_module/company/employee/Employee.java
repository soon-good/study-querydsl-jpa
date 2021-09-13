package io.study.qdsl.single_module.company.employee;

import io.study.qdsl.single_module.company.department.Department;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "EMP", schema = "public")
@ToString(exclude = "dept")
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EMPLOYEE_ID")
    private Long id;

    @Column(name = "EMPLOYEE_NAME")
    private String name;

    @Column(name = "EMPLOYEE_SALARY")
    private Double salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID")
    private Department dept;

    public Employee(){}

    @Builder
    public Employee(Long id, String name, Department department){
        this.id = id;
        this.name = name;
        this.dept = department;
    }

    public void changeName(String name){
        this.name = name;
    }

    public void setDept(Department dept){
        this.dept = dept;
    }

    public void assignDept(Department dept){
        this.dept = dept;
        dept.getEmployees().add(this);
    }

    public void changeDept(Department moveTo){
        // 변경 전 부서 내의 사원 목록에서 remove
        this.dept.getEmployees().remove(this);
        // 변경하려는 부서로 부서 변경
        this.dept = moveTo;
        // 변경하려는 부서내의 사원 목록에 add
        this.dept.getEmployees().add(this);
    }

    public void deleteDept(Long id){
        this.dept.getEmployees().remove(this);
        this.dept = null;
    }
}
