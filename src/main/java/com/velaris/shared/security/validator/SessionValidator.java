package com.velaris.shared.security.validator;

import java.util.UUID;

public interface SessionValidator {
    boolean isSessionValid(UUID sessionId);
}
