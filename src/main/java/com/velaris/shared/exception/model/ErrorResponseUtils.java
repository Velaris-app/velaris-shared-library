package com.velaris.shared.exception.model;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;

@UtilityClass
public class ErrorResponseUtils {

    public ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return build(status, message, null);
    }

    public ResponseEntity<ErrorResponse> build(HttpStatus status, String message, Map<String, String> details) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message != null ? message : "Unexpected error")
                .details(details != null && !details.isEmpty() ? details : null)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}