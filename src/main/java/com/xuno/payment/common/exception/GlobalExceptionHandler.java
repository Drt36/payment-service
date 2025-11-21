package com.xuno.payment.common.exception;


import com.xuno.payment.common.dto.GlobalApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalApiResponse<List<Map<String, String>>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validation error occurred: {}", ex.getMessage());
        
        List<Map<String, String>> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    Map<String, String> errorMap = new HashMap<>();
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errorMap.put("field", fieldName);
                    errorMap.put("message", errorMessage);
                    return errorMap;
                })
                .toList();

        GlobalApiResponse<List<Map<String, String>>> response = GlobalApiResponse.error(
                errors,
                HttpStatus.BAD_REQUEST, 
                "Validation failed"
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        log.warn("Illegal argument error occurred: {}", ex.getMessage());
        
        GlobalApiResponse<Void> response = GlobalApiResponse.error(
                HttpStatus.BAD_REQUEST, 
                ex.getMessage()
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        GlobalApiResponse<Void> response = GlobalApiResponse.error(
                HttpStatus.NOT_FOUND, 
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        
        log.warn("JSON parsing error occurred: {}", ex.getMessage());
        
        String message = "Invalid JSON format in request body";
        if (ex.getMessage() != null && ex.getMessage().contains("JSON parse error")) {
            message = "Invalid JSON format: " + ex.getMessage();
        }
        
        GlobalApiResponse<Void> response = GlobalApiResponse.error(
                HttpStatus.BAD_REQUEST, 
                message
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        
        log.warn("Type mismatch error occurred: {}", ex.getMessage());
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s", 
                ex.getValue(), 
                ex.getName(), 
                ex.getRequiredType().getSimpleName());
        
        GlobalApiResponse<Void> response = GlobalApiResponse.error(
                HttpStatus.BAD_REQUEST, 
                message
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleGenericException(Exception ex) {
        
        log.error("Unexpected error occurred", ex);
        
        GlobalApiResponse<Void> response = GlobalApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred. Please try again later."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
