package com.monocept.myapp.exception;

import org.springframework.http.HttpStatus;

public class BankApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private HttpStatus status;
    private String message;

    public BankApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public BankApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
