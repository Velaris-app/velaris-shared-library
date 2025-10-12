package com.velaris.shared.security;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {
    public static Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new SecurityException("Unauthorized");

        if (auth.getPrincipal() instanceof SecurityUserDetails user) return user.getId();
        throw new SecurityException("Unauthorized");
    }
}
