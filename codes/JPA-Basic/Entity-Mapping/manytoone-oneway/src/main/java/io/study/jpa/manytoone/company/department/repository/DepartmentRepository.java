package io.study.jpa.manytoone.company.department.repository;

import io.study.jpa.manytoone.company.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
