package io.study.qdsl.single_module.company.department.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.study.qdsl.single_module.company.department.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

	List<Department> findDepartmentByDeptName(String name);
}
