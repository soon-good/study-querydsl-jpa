package io.study.jpa.generation_strategy.company.department.repository;

import io.study.jpa.generation_strategy.company.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
