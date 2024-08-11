package com.monocept.myapp.exception;

public class NoContactDetailRecordFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoContactDetailRecordFoundException(String message) {
		super(message);
	}

}
