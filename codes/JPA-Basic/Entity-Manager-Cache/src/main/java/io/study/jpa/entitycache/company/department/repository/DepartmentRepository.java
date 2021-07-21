package io.study.jpa.entitycache.company.department.repository;

import io.study.jpa.entitycache.company.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
