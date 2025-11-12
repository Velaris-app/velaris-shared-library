package com.velaris.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String messageKey;
    private final Map<String, String> details;

    public BusinessException(HttpStatus status, String messageKey) {
        this(status, messageKey, Collections.emptyMap());
    }

    public BusinessException(HttpStatus status, String messageKey, Map<String, String> details) {
        super(messageKey);
        this.status = status;
        this.messageKey = messageKey;
        this.details = details != null ? details : Collections.emptyMap();
    }

    public static BusinessException notFound(String messageKey) {
        return new BusinessException(HttpStatus.NOT_FOUND, messageKey);
    }

    public static BusinessException forbidden(String messageKey) {
        return new BusinessException(HttpStatus.FORBIDDEN, messageKey);
    }

    public static BusinessException badRequest(String messageKey) {
        return new BusinessException(HttpStatus.BAD_REQUEST, messageKey);
    }

    public static BusinessException conflict(String messageKey) {
        return new BusinessException(HttpStatus.CONFLICT, messageKey);
    }

    public static BusinessException of(HttpStatus status, String messageKey, Map<String, String> details) {
        return new BusinessException(status, messageKey, details);
    }
}
