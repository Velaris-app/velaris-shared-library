package com.velaris.shared.security.jwt;

import com.velaris.shared.config.JwtProperties;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.velaris.shared.security.jwt.JwtClaims.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final Key key;
    private final JwtProperties properties;

    // ---------------- GENERATE TOKEN ----------------
    public String generateAccessToken(UUID userId, String... roles) {
        return generateToken(userId, UUID.randomUUID(), properties.getAccessExpirationMs(), ACCESS, roles);
    }

    public String generateRefreshToken(UUID userId) {
        return generateToken(userId, UUID.randomUUID(), properties.getRefreshExpirationMs(), REFRESH);
    }

    public String generateRefreshToken(UUID userId, UUID sessionId) {
        return generateToken(userId, sessionId, properties.getRefreshExpirationMs(), REFRESH);
    }

    private String generateToken(UUID userId, UUID jti, long validityMs, String type, String... roles) {
        JwtBuilder builder = buildToken(userId.toString(), validityMs)
                .setId(jti.toString())
                .claim(TOKEN_TYPE, type);

        if (roles != null && roles.length > 0) {
            builder.claim(ROLES, roles);
        }

        return builder.compact();
    }

    // ---------------- VALIDATE ----------------
    public boolean validateAccessToken(String token) {
        return validateToken(token);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token);
    }

    // ---------------- CLAIMS ----------------
    public UUID getUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public UUID getJti(String token) {
        String jti = getClaims(token).getId();
        return jti != null ? UUID.fromString(jti) : null;
    }

    public List<String> getRoles(String token) {
        Object roles = getClaims(token).get(ROLES);
        return roles instanceof List<?> list ? list.stream().map(Object::toString).toList() : List.of();
    }

    public String getTokenType(String token) {
        return getClaims(token).get(TOKEN_TYPE, String.class);
    }

    public long getExpiresIn(String token) {
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
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(validityMs)))
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