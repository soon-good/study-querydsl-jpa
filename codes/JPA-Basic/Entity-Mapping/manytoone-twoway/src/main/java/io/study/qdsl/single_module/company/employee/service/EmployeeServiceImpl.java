package io.study.qdsl.single_module.company.employee.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.study.qdsl.single_module.company.department.Department;
import io.study.qdsl.single_module.company.department.repository.DepartmentRepository;
import io.study.qdsl.single_module.company.employee.Employee;
import io.study.qdsl.single_module.company.employee.repository.EmployeeRepository;
import io.study.qdsl.single_module.company.exception.NoDepartmentFoundException;
import io.study.qdsl.single_module.company.exception.NoEmployeeFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;

	@Transactional
	@Override
	public void modifyEmployeeName(Long id, String name) {
		Employee targetEmployee = employeeRepository.findById(id)
			.orElseThrow(() -> new NoEmployeeFoundException("해당 사원이 존재하지 않습니다."));

		targetEmployee.changeName(name);
	}

	@Transactional
	@Override
	public void deleteEmployeeName(Long id) {
		Employee deleteEmployee = employeeRepository.findById(id)
			.orElseThrow(() -> new NoEmployeeFoundException("해당 사원이 존재하지 않습니다."));
		employeeRepository.delete(deleteEmployee);
	}

	@Transactional
	@Override
	public void changeDept(Long id, Long deptId) {
		Employee targetEmployee = employeeRepository.findById(id)
			.orElseThrow(() -> new NoEmployeeFoundException("해당 사원이 존재하지 않습니다."));

		Department department = departmentRepository.findById(deptId)
			.orElseThrow(() -> new NoDepartmentFoundException("해당 부서가 존재하지 않습니다."));

		targetEmployee.changeDept(department);
		employeeRepository.save(targetEmployee);
	}

	@Transactional
	@Override
	public void removeEmployee(Long id) {
		Employee employee = employeeRepository.findById(id)
			.orElseThrow(() -> new NoEmployeeFoundException("해당 사원이 존재하지 않습니다"));
		employee.deleteDept(id);
		employeeRepository.delete(employee);
	}
}
