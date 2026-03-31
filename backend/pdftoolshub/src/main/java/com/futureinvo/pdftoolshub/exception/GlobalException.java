package com.futureinvo.pdftoolshub.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> handleCustom(CustomException ex){
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception ex){
		return ResponseEntity.status(500).body("Something wnet Wrong");
	}
}
