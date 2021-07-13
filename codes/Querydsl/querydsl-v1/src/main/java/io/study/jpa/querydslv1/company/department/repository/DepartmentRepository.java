package io.study.jpa.querydslv1.company.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.study.jpa.querydslv1.company.department.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
