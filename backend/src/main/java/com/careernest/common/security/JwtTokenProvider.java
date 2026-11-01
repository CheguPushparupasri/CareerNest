package com.careernest.common.security;

import com.careernest.modules.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long accessTokenMinutes;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-minutes}") long accessTokenMinutes
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32
                || secret.toLowerCase().contains("replace") || secret.toLowerCase().contains("change")) {
            throw new IllegalStateException("JWT_SECRET must be a non-placeholder secret of at least 32 bytes");
        }
        if (accessTokenMinutes <= 0) {
            throw new IllegalStateException("JWT access token lifetime must be positive");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMinutes = accessTokenMinutes;
    }

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenMinutes * 60);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getPublicId())
                .claim("role", user.getRole().getName().name())
                .claim("type", "ACCESS")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    public String getSubject(String token) {
        return claims(token).getSubject();
    }

    public boolean isValid(String token) {
        Claims claims = claims(token);
        return "ACCESS".equals(claims.get("type", String.class));
    }

    public long getAccessTokenSeconds() {
        return accessTokenMinutes * 60;
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
