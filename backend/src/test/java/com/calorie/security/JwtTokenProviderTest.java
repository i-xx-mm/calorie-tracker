package com.calorie.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setup() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(
                tokenProvider,
                "jwtSecret",
                "test-secret-key-at-least-32-chars-long-!"
        );
        ReflectionTestUtils.setField(
                tokenProvider,
                "jwtExpirationMs",
                3600000L
        );
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = tokenProvider
                .generateToken("testuser");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsernameFromToken_shouldReturnCorrectUsername() {
        String token = tokenProvider
                .generateToken("testuser");
        String username = tokenProvider
                .getUsernameFromToken(token);
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_validToken_shouldReturnTrue() {
        String token = tokenProvider
                .generateToken("testuser");
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    void validateToken_invalidToken_shouldReturnFalse() {
        assertFalse(
                tokenProvider.validateToken("invalid.token.here")
        );
    }

    @Test
    void validateToken_emptyToken_shouldReturnFalse() {
        assertFalse(tokenProvider.validateToken(""));
    }

    @Test
    void getExpirationTime_shouldReturn3600000() {
        assertEquals(
                3600000L,
                tokenProvider.getExpirationTime()
        );
    }
}