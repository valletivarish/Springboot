package com.monocept.myapp.exception;

import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class BankResponseExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(BankResponseExceptionHandler.class);
	@ExceptionHandler
	public ResponseEntity<BankErrorResponse> handleException(BankApiException exc) {
		BankErrorResponse error = new BankErrorResponse();
		logger.error(exc.getMessage());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMessage(exc.getMessage());
		error.setTimeStamp(System.currentTimeMillis());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<BankErrorResponse> handleException(AccessDeniedException exc) {

		BankErrorResponse error = new BankErrorResponse();
		logger.error(exc.getMessage());
		error.setStatus(HttpStatus.UNAUTHORIZED.value());
		error.setMessage(exc.getClass().getSimpleName());
		error.setTimeStamp(System.currentTimeMillis());

		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler
	public ResponseEntity<BankErrorResponse> handleException(Exception exc) {

		BankErrorResponse error = new BankErrorResponse();
		logger.error(exc.getMessage());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMessage(exc.getClass().getSimpleName());
		exc.printStackTrace();
		error.setTimeStamp(System.currentTimeMillis());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	

	@ExceptionHandler
	public ResponseEntity<BankErrorResponse> handleException(NoRecordFoundException exc) {

		BankErrorResponse error = new BankErrorResponse();
		logger.error(exc.getMessage());
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setMessage(exc.getMessage());
		error.setTimeStamp(System.currentTimeMillis());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
}