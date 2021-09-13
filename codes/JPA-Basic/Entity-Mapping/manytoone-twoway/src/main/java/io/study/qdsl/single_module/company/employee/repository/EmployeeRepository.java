package io.study.qdsl.single_module.company.employee.repository;

import java.util.List;

import io.study.qdsl.single_module.company.employee.Employee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	List<Employee> findEmployeeByName(String name);

	@Query("select e from Employee e join fetch e.dept where e.name = :name")
	List<Employee> findEmployeesByNameEquals(@Param("name") String name);

	@EntityGraph(attributePaths = {"dept"})
	@Query("select e from Employee e where e.name = :name")
	List<Employee> findEmployeesEntityGraph(@Param("name") String name);

	@Override
	@EntityGraph(attributePaths = {"dept"})
	List<Employee> findAll();
}
