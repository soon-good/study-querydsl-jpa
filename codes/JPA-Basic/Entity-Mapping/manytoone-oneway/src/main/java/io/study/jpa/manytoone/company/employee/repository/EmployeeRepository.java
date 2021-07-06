package io.study.jpa.manytoone.company.employee.repository;

import io.study.jpa.manytoone.company.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
