package com.jfecm.bankaccountmanagement.exceptions;

import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> handleNumberFormatException(NumberFormatException e) {
        return createErrorResponse("Invalid input format. Please provide a valid number.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleNumberFormatException(MissingServletRequestParameterException e) {
        return createErrorResponse("Required parameter(s) are missing in the request. Please ensure that all necessary parameters are provided.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("type: " + e.getParameter().getParameterType());

        if (e.getParameter().getParameterType() == UserStatus.class) {
            return createErrorResponse("Invalid User Status provided.", HttpStatus.BAD_REQUEST);
        }

        if (e.getParameter().getParameterType() == BankingAccountStatus.class) {
            return createErrorResponse("Invalid Banking Account Status provided.", HttpStatus.BAD_REQUEST);
        }

        if (e.getParameter().getParameterType() == AccountTransactionType.class) {
            return createErrorResponse("Invalid Banking Account Transaction Type provided.", HttpStatus.BAD_REQUEST);
        }

        if (e.getParameter().getParameterType() == Double.class || e.getParameter().getParameterType() == Long.class) {
            return createErrorResponse("Invalid input format. Please provide a valid number.", HttpStatus.BAD_REQUEST);
        }

        if (e.getParameter().getParameterType() == LocalDate.class) {
            return createErrorResponse("Invalid input format. Please provide a valid date in YYYY-MM-DD format.", HttpStatus.BAD_REQUEST);
        }

        return createErrorResponse("Invalid input format. Details: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InsufficientFundsException.class, InactiveAccountException.class, InvalidTransactionException.class, DniAlreadyExistsException.class, EmailDuplicateException.class})
    public ResponseEntity<Map<String, Object>> handleConflictExceptions(RuntimeException e) {
        return createErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String errorMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>(Map.of("Result", errorMessage), httpStatus);
    }
}
