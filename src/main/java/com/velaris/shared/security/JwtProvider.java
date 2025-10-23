package com.velaris.shared.security;

import com.velaris.shared.config.JwtProperties;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.velaris.shared.security.JwtClaims.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final Key key;
    private final JwtProperties properties;

    // ---------------- ACCESS TOKEN ----------------
    public String generateAccessToken(Long userId, String... roles) {
        JwtBuilder builder = buildToken(userId.toString(), properties.getAccessExpirationMs())
                .claim(TOKEN_TYPE, ACCESS);

        if (roles != null && roles.length > 0) {
            builder.claim(ROLES, roles);
        }

        return builder.compact();
    }

    public List<String> getRolesFromAccessToken(String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object roles = claims.get(ROLES);
        if (roles instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token);
    }

    public Long getUserIdFromAccessToken(String token) {
        return Long.valueOf(getUserId(token));
    }

    // ---------------- REFRESH TOKEN ----------------
    public String generateRefreshToken(Long userId) {
        return buildToken(userId.toString(), properties.getRefreshExpirationMs())
                .setId(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE, REFRESH)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token);
    }

    public Long getUserIdFromRefreshToken(String token) {
        return Long.valueOf(getUserId(token));
    }

    public String getJtiFromRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    // ---------------- HELPERS ----------------
    public String getTokenType(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(TOKEN_TYPE, String.class);
    }

    public long getExpiresInFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Instant now = Instant.now();
        Instant exp = claims.getExpiration().toInstant();
        return Math.max(0, exp.getEpochSecond() - now.getEpochSecond());
    }

    // ---------------- PRIVATE ----------------
    private JwtBuilder buildToken(String subject, long validityMs) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(validityMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.HS256);
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}