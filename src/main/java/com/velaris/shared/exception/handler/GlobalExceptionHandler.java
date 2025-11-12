package com.velaris.shared.exception.handler;

import com.velaris.shared.exception.BusinessException;
import com.velaris.shared.exception.model.ErrorResponse;
import com.velaris.shared.exception.model.ErrorResponseUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // === VALIDATION ===
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            details.put(field, "error.field_invalid");
        });
        log.warn("Validation error: {}", details);
        return ErrorResponseUtils.build(HttpStatus.BAD_REQUEST, "error.validation_failed", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                details.put(v.getPropertyPath().toString(), "error.field_invalid"));
        log.warn("Constraint violation: {}", details);
        return ErrorResponseUtils.build(HttpStatus.BAD_REQUEST, "error.constraint_violation", details);
    }

    // === REQUEST / MEDIA / PARAM ERRORS ===
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMessageNotReadableException.class,
            NoHandlerFoundException.class
    })
    protected ResponseEntity<ErrorResponse> handleRequestIssues(Exception ex) {
        log.warn("Request issue: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.BAD_REQUEST, "error.request_invalid");
    }

    // === SECURITY ===
    @ExceptionHandler({ AccessDeniedException.class })
    protected ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.FORBIDDEN, "error.access_denied");
    }

    // === DATA / DATABASE ===
    @ExceptionHandler({ EntityNotFoundException.class })
    protected ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.NOT_FOUND, "error.entity_not_found");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.CONFLICT, "error.data_integrity_violation");
    }

    // === FILE UPLOAD ===
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorResponse> handleMaxUpload(MaxUploadSizeExceededException ex) {
        log.warn("File too large: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.PAYLOAD_TOO_LARGE, "error.file_too_large");
    }

    // === GENERIC FALLBACK ===
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.BAD_REQUEST, "error.illegal_argument");
    }

    // === PATH / BINDING / TYPE CONVERSION ===
    @ExceptionHandler({
            TypeMismatchException.class,
            MethodArgumentTypeMismatchException.class,
            MissingPathVariableException.class,
            ServletRequestBindingException.class
    })
    protected ResponseEntity<ErrorResponse> handleTypeOrBindingErrors(Exception ex) {
        log.warn("Binding/type error: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.BAD_REQUEST, "error.invalid_parameter");
    }

    // === SERIALIZATION ===
    @ExceptionHandler(HttpMessageNotWritableException.class)
    protected ResponseEntity<ErrorResponse> handleSerializationError(HttpMessageNotWritableException ex) {
        log.error("Serialization error: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.INTERNAL_SERVER_ERROR, "error.serialization_failed");
    }

    // === MULTIPART / FILE UPLOAD EDGE CASE ===
    @ExceptionHandler(MultipartException.class)
    protected ResponseEntity<ErrorResponse> handleMultipartError(MultipartException ex) {
        log.warn("Multipart error: {}", ex.getMessage());
        return ErrorResponseUtils.build(HttpStatus.BAD_REQUEST, "error.invalid_multipart");
    }

    // === BUSINESS EXCEPTIONS ===
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {} | details: {}", ex.getMessageKey(), ex.getDetails());
        return ErrorResponseUtils.build(
                ex.getStatus(),
                ex.getMessageKey(),
                ex.getDetails()
        );
    }

    // === GENERIC FALLBACK ===
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ErrorResponseUtils.build(HttpStatus.INTERNAL_SERVER_ERROR, "error.unexpected");
    }
}