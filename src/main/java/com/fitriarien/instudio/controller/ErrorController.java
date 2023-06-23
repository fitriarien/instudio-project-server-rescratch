package com.fitriarien.instudio.controller;

import com.fitriarien.instudio.model.response.GenerateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GenerateResponse<String>> constraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GenerateResponse.<String>builder().errors(exception.getMessage()).build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<GenerateResponse<String>> apiException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getRawStatusCode())
                .body(GenerateResponse.<String>builder().errors(exception.getReason()).build());
    }
}
