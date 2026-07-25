package com.urbanshopper.shared.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusiness(BusinessException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation() {
        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", "Invalid input"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric() {
        return ResponseEntity.internalServerError().body(ApiResponse.error("INTERNAL_ERROR", "An error occurred"));
    }
}
