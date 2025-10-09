package com.velaris.shared.exception;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ErrorResponseUtils.build(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ErrorResponseUtils.build(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.error("AccessDeniedException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                ErrorResponseUtils.build(HttpStatus.FORBIDDEN, "Access denied"),
                HttpStatus.FORBIDDEN
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        log.error("Validation failed: {}", errors);

        return createResponseEntity(errors, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                ErrorResponseUtils.build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> createResponseEntity(
            @Nullable Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request
    ) {
        if (body instanceof ErrorResponse error) {
            return new ResponseEntity<>(error, headers, statusCode);
        }

        if (body instanceof Map<?, ?> errors) {
            Map<String, String> details = new HashMap<>();
            errors.forEach((k, v) -> details.put(String.valueOf(k), String.valueOf(v)));
            return new ResponseEntity<>(
                    ErrorResponseUtils.build(HttpStatus.valueOf(statusCode.value()), "Validation error", details),
                    HttpStatus.valueOf(statusCode.value())
            );
        }

        return new ResponseEntity<>(
                ErrorResponseUtils.build(HttpStatus.valueOf(statusCode.value()), body != null ? body.toString() : "Unexpected error"),
                HttpStatus.valueOf(statusCode.value())
        );
    }
}
