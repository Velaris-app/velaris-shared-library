package com.velaris.shared.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.util.Map;

@UtilityClass
public class ErrorResponseUtils {

    public ErrorResponse build(HttpStatus status, String message) {
        return build(status, message, null);
    }

    public ErrorResponse build(HttpStatus status, String message, Map<String, String> details) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .details(details)
                .build();
    }
}
