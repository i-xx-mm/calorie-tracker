package com.calorie.controller;

import com.calorie.dto.BMIResponse;
import com.calorie.dto.UserProfileDto;
import com.calorie.exception.ForbiddenException;
import com.calorie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getProfile(
            @PathVariable String username,
            Authentication authentication) {
        String authenticatedUsername = authentication.getName();
        if (!username.equals(authenticatedUsername)) {
            throw new ForbiddenException("You can only access your own profile");
        }
        UserProfileDto profile = userService.getProfile(username);
        return ResponseEntity.ok(profile);
    }

    /**
     * Get current authenticated user's profile
     * No path variable needed – uses JWT token to identify user
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        UserProfileDto profile = userService.getProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserProfileDto> updateProfile(
            @PathVariable String username,
            @Valid @RequestBody UserProfileDto profileDto,
            Authentication authentication) {
        String authenticatedUsername = authentication.getName();
        if (!username.equals(authenticatedUsername)) {
            throw new ForbiddenException("You can only update your own profile");
        }
        UserProfileDto updated = userService.updateProfile(username, profileDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{username}/bmi")
    public ResponseEntity<BMIResponse> getBMI(
            @PathVariable String username,
            Authentication authentication) {
        String authenticatedUsername = authentication.getName();
        if (!username.equals(authenticatedUsername)) {
            throw new ForbiddenException("You can only access your own BMI data");
        }
        BMIResponse bmi = userService.calculateBMI(username);
        return ResponseEntity.ok(bmi);
    }
}