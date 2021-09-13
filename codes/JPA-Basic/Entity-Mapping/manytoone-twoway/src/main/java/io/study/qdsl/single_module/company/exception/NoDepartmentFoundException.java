package io.study.qdsl.single_module.company.exception;

public class NoDepartmentFoundException extends RuntimeException{

	public NoDepartmentFoundException(String message){
		super(message);
	}

	public NoDepartmentFoundException(String message, Throwable throwable){
		super(message, throwable);
	}

	public NoDepartmentFoundException(Throwable throwable){
		super(throwable);
	}
}
