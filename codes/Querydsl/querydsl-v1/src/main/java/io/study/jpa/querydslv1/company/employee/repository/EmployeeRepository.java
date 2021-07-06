package io.study.jpa.querydslv1.company.employee.repository;

import io.study.jpa.querydslv1.company.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
