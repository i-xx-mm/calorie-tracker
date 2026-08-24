package com.calorie.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT utility responsible for token generation, parsing, and validation
 * Uses HMAC-SHA signing algorithm. Secret key and expiration duration are injected from application configuration
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Generates signed JWT access token for given username
     * Subject claim stores username, includes issued-at and expiration timestamps
     *
     * @param username authenticated user identifier stored inside token subject
     * @return serialized JWT string
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Parses valid JWT and extracts username stored in subject claim
     *
     * @param token JWT token string
     * @return username value from token subject
     * @throws JwtException if token signature invalid or token malformed
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validates JWT signature and structural integrity
     * Catches all JWT parsing related exceptions and logs error details
     *
     * @param token JWT token string
     * @return true when signature passes verification; false for tampered, malformed, or expired tokens
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Build HMAC-SHA signing secret key from configured secret string
     *
     * @return cryptographic SecretKey for signing and verifying JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Returns configured JWT expiration value in milliseconds
     *
     * @return token expiry time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpirationMs;
    }
}