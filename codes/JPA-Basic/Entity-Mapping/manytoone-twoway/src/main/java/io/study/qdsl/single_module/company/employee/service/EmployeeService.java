package io.study.qdsl.single_module.company.employee.service;

import io.study.qdsl.single_module.company.department.Department;

public interface EmployeeService {

	public void modifyEmployeeName(Long id, String name);

	public void deleteEmployeeName(Long id);

	public void changeDept(Long id, Long deptId);

	public void removeEmployee(Long id);
}
