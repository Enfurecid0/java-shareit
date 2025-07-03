package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception e) {
        log.warn("Validation exception: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundExceptions(Exception e) {
        log.warn("Not found exception: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Method argument not valid exception: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();

        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            if (errorMessage.length() > 0) {
                errorMessage.append(", ");
            }
            errorMessage.append(error.getDefaultMessage());
        });

        errorResponse.put("error", errorMessage.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception e) {
        log.error("General exception: {}", e.getMessage(), e);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal server error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenExceptions(Exception e) {
        log.warn("Forbidden exception: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<Map<String, String>> handleInternalServerErrorExceptions(Exception e) {
        log.error("Internal server error: {}", e.getMessage(), e);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}