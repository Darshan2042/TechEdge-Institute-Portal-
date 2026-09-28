package com.techedge.portal.security;

import com.techedge.portal.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessExpiryMs;
    private final long refreshExpiryMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiry-ms}") long accessExpiryMs,
            @Value("${jwt.refresh-expiry-ms}") long refreshExpiryMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.accessExpiryMs = accessExpiryMs;
        this.refreshExpiryMs = refreshExpiryMs;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessExpiryMs);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshExpiryMs);
    }

    private String generateToken(User user, long expiryMs) {

        Date issuedAt = new Date();
        Date expiration = new Date(
                issuedAt.getTime() + expiryMs
        );

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {

        return Long.valueOf(
                extractClaims(token).getSubject()
        );
    }

    public String extractEmail(String token) {

        return extractClaims(token)
                .get("email", String.class);
    }

    public String extractRole(String token) {

        return extractClaims(token)
                .get("role", String.class);
    }

    public boolean isTokenValid(String token) {

        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessExpirySeconds() {
        return accessExpiryMs / 1000;
    }
}