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
    public String generateAccessToken(UUID userId, String... roles) {
        JwtBuilder builder = buildToken(userId.toString(), properties.getAccessExpirationMs())
                .claim(TOKEN_TYPE, ACCESS);
        if (roles != null && roles.length > 0) {
            builder.claim(ROLES, roles);
        }
        return builder.compact();
    }

    public List<String> getRolesFromAccessToken(String token) {
        return getClaims(token).getOrDefault(ROLES, List.of())
                instanceof List<?> list ? list.stream().map(Object::toString).toList() : List.of();
    }

    public UUID getUserIdFromAccessToken(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token);
    }

    // ---------------- REFRESH TOKEN ----------------
    public String generateRefreshToken(UUID userId) {
        return buildToken(userId.toString(), properties.getRefreshExpirationMs())
                .setId(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE, REFRESH)
                .compact();
    }

    public String generateRefreshToken(UUID userId, UUID sessionId) {
        return buildToken(userId.toString(), properties.getRefreshExpirationMs())
                .setId(sessionId.toString())
                .claim(TOKEN_TYPE, REFRESH)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token);
    }

    public UUID getUserIdFromRefreshToken(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public String getJtiFromRefreshToken(String token) {
        return getClaims(token).getId();
    }

    // ---------------- HELPERS ----------------
    public String getTokenType(String token) {
        return getClaims(token).get(TOKEN_TYPE, String.class);
    }

    public long getExpiresInFromToken(String token) {
        Instant now = Instant.now();
        Instant exp = getClaims(token).getExpiration().toInstant();
        return Math.max(0, exp.getEpochSecond() - now.getEpochSecond());
    }

    // ---------------- PRIVATE ----------------
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

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
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}