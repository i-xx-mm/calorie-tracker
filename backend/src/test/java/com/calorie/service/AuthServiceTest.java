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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateAccountAndUser_returnToken() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("test1");
        req.setPassword("pass123");
        req.setHeight(160);
        req.setWeight(52);
        req.setAge(26);
        req.setGender("F");

        when(accountRepository.existsByUsername("test1")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(tokenProvider.generateToken("test1")).thenReturn("mock-jwt");
        when(tokenProvider.getExpirationTime()).thenReturn(3600L);

        AuthResponse resp = authService.register(req);

        assertEquals("test1", resp.getUsername());
        assertEquals("mock-jwt", resp.getToken());
        verify(accountRepository).save(any(Account.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_usernameExists_throwConflict() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("test1");

        when(accountRepository.existsByUsername("test1")).thenReturn(true);
        assertThrows(ConflictException.class, () -> authService.register(req));
        verify(accountRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_correctCredential_returnToken() {
        LoginRequest req = new LoginRequest();
        req.setUsername("test1");
        req.setPassword("pass123");

        Account account = new Account();
        account.setUsername("test1");
        account.setPassword("encodedPass");

        when(accountRepository.findByUsername("test1")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("pass123", "encodedPass")).thenReturn(true);
        when(tokenProvider.generateToken("test1")).thenReturn("mock-jwt");
        when(tokenProvider.getExpirationTime()).thenReturn(3600L);

        AuthResponse resp = authService.login(req);
        assertEquals("test1", resp.getUsername());
        assertEquals("mock-jwt", resp.getToken());
    }

    @Test
    void login_wrongPassword_throwUnauthorized() {
        LoginRequest req = new LoginRequest();
        req.setUsername("test1");
        req.setPassword("wrong");

        Account account = new Account();
        account.setUsername("test1");
        account.setPassword("encodedPass");

        when(accountRepository.findByUsername("test1")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }

    @Test
    void login_userNotFound_throwUnauthorized() {
        LoginRequest req = new LoginRequest();
        req.setUsername("notexist");
        req.setPassword("xxx");

        when(accountRepository.findByUsername("notexist")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }
}