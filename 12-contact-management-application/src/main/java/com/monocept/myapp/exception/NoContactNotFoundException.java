package com.monocept.myapp.exception;

public class NoContactNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public NoContactNotFoundException(String message) {
		super(message);
	}

	
}