package io.study.jpa.key_combination.company.employee;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class EmployeeId implements Serializable {
	private Long id;
	private String email;

	public EmployeeId(){}
}
