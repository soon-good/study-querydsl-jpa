package io.study.jpa.generation_strategy.company.employee.repository;

import io.study.jpa.generation_strategy.company.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
