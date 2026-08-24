package com.calorie.controller;

import com.calorie.dto.AuthResponse;
import com.calorie.dto.LoginRequest;
import com.calorie.dto.RegisterRequest;
import com.calorie.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST controller
 * Handles user registration, login, and logout operations, issues JWT access token
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Register new user account
     * Applies input validation and COPPA age restriction
     *
     * @param request registration payload containing username, password, and physical metrics
     * @return JWT token response, returns 201 Created
     * Example
     * POST /api/auth/register
     * Request Body:
     * {
     *   "username": "testUser",
     *   "password": "testPassword",
     *   "age":24,
     *   "gender":"female",
     *   "height":170,
     *   "weight":55
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Authenticate existing user with username and password
     *
     * @param request login credentials payload
     * @return JWT bearer token for subsequent authenticated requests
     * Example:
     * POST /api/auth/login
     * Request Body:
     * {
     *   "username":"testUser",
     *   "password":"testPassword"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout endpoint
     * Client should discard local JWT token after receiving response
     * Server‑side token blacklist is NOT implemented
     *
     * @return 204 No‑Content
     * Example: POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}