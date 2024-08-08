package com.monocept.myapp.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class StudentExceptionHandler {
	@ExceptionHandler
	public ResponseEntity<ResponseErrorNotFound> execeptionHandler(StudentNotFoundException ex){
		ResponseErrorNotFound error=new ResponseErrorNotFound();
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setMessage(ex.getMessage());
		error.setDate(LocalDateTime.now());
		
		
		return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
		
	}
}
