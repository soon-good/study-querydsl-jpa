package io.study.qdsl.single_module.company.exception;

public class NoEmployeeFoundException extends RuntimeException{

	public NoEmployeeFoundException(String message){
		super(message);
	}

	public NoEmployeeFoundException(String message, Throwable cause){
		super(message, cause);
	}

	public NoEmployeeFoundException(Throwable cause){
		super(cause);
	}

}
