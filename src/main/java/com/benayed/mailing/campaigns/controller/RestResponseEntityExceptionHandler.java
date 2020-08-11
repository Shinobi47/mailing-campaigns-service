package com.benayed.mailing.campaigns.controller;

import java.io.UncheckedIOException;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.benayed.mailing.campaigns.exception.TechnicalException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ResponseBody
	@ExceptionHandler(value = { IllegalArgumentException.class, TechnicalException.class})
	protected ResponseEntity<?> handleBadRequest(RuntimeException e) {
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = { UncheckedIOException.class})
	protected ResponseEntity<?> handleUncheckedIOException(RuntimeException e) {
		log.error("File processing error !", e);
		return new ResponseEntity<String>("Server error when processing a file, please contact your administrator", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(value = {NoSuchElementException.class})
	protected ResponseEntity<?> handleNoSuchElementException(RuntimeException e) {
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
}
	