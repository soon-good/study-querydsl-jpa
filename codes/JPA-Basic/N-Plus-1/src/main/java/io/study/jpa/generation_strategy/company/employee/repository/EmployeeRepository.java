package io.study.jpa.generation_strategy.company.employee.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import io.study.jpa.generation_strategy.company.employee.Employee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	@EntityGraph(attributePaths = {"dept"})
	@Query("select e from Employee e join e.dept d")
	public List<Employee> findAllUsingEntityGraph();

	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	public Optional<Employee> findReadOnlyById(Long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public Optional<Employee> findLockById(Long id);
}
