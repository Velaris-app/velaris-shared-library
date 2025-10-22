package com.velaris.shared.config;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.Key;

@Configuration
@RequiredArgsConstructor
public class JwtKeyConfig {
    private final JwtProperties properties;

    @Bean
    public Key jwtSigningKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }
}
