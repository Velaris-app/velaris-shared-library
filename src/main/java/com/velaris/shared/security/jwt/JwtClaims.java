package com.velaris.shared.security.jwt;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class JwtClaims {
    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";
    public static final String ROLES = "roles";
}
