package com.mycorp.arithmeticcalculator.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleProcessException extends RuntimeException {
	
	private static final long serialVersionUID = -3128681006635769411L;

	public RoleProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoleProcessException(String message) {
		super(message);
	}

	public RoleProcessException(Throwable cause) {
		super(cause);
	}


	
}
