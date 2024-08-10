package com.monocept.myapp.exception;

public class NoUserRecordFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoUserRecordFoundException(String message) {
		super(message);
	}
}