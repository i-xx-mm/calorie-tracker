package com.calorie.service;

import com.calorie.dto.AuthResponse;
import com.calorie.dto.LoginRequest;
import com.calorie.dto.RegisterRequest;
import com.calorie.exception.ConflictException;
import com.calorie.exception.UnauthorizedException;
import com.calorie.model.Account;
import com.calorie.model.User;
import com.calorie.repository.AccountRepository;
import com.calorie.repository.UserRepository;
import com.calorie.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Authentication service handling user registration and login\
 * Manages account creation, password hashing, user profile initialization, and JWT token issuance
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * Register new user account
     * Creates credential Account entity and corresponding health-profile User entity
     * Password will be encoded before persistence. Returns signed JWT upon successful registration
     *
     * @param request registration payload contain username, password, and user health metrics
     * @return AuthResponse with JWT token, username, and token expiration value
     * @throws ConflictException if requested username already exists in database
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username '" + request.getUsername() + "' already exists");
        }

        // Create account
        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();
        accountRepository.save(account);

        // Create user profile with health metrics
        User user = User.builder()
                .username(request.getUsername())
                .height(request.getHeight())
                .currentWeight(request.getWeight().doubleValue())
                .age(request.getAge())
                .gender(request.getGender())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Generate token
        String token = tokenProvider.generateToken(request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(request.getUsername())
                .expiresIn(tokenProvider.getExpirationTime())
                .build();
    }

    /**
     * Authenticate existing user by username and password
     * Validates credentials against stored hashed password, return JWT token for subsequent request
     *
     * @param request login payload with username and password
     * @return AuthResponse with JWT token, username, and token expiration value
     * @throws UnauthorizedException when username cannot be found or password mismatch
     */
    public AuthResponse login(LoginRequest request) {
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(account.getUsername())
                .expiresIn(tokenProvider.getExpirationTime())
                .build();
    }
}