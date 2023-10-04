package com.jfecm.bankaccountmanagement.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> handleNumberFormatException(NumberFormatException e) {
        return createErrorResponse("Invalid input format. Please provide a valid number.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidFormatException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidFormatException(RuntimeException e) {
        return createErrorResponse("Invalid input format. Details: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InsufficientFundsException.class,
            InactiveAccountException.class,
            InvalidTransactionException.class,
            InvalidStatusException.class,
            DniAlreadyExistsException.class,
            EmailDuplicateException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflictExceptions(RuntimeException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String errorMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>(Map.of("Result", errorMessage), httpStatus);
    }
}
