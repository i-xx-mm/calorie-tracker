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

/**
 * User profile REST controller
 * Read and update user physical profile data, compute BMI value
 * Enforces resource-based authorization: user may only access or modify their own profile
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Fetch user profile by username path variable
     * Rejects request if authenticated user tries to read other user's profile
     *
     * @param username target user identifier from url path
     * @param authentication spring security authentication holding logged‑in username
     * @return complete UserProfileDto including server‑computed goalCalories
     * @throws ForbiddenException when accessing another user's profile
     * Example: GET /api/users/testUser
     */
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
     * Get profile for currently authenticated user
     * Extract username from JWT token without path variable
     * @param authentication spring security authentication holding logged-in username
     * @return complete UserProfileDto of current user
     * Example: GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        UserProfileDto profile = userService.getProfile(username);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update user physical profile
     * goalCalories field inside DTO is ignored; recalculated server-side
     *
     * @param username target user identifier from url path
     * @param profileDto incoming profile payload
     * @param authentication spring security authentication holding logged-in username
     * @return refreshed UserProfileDto after update
     * @throws ForbiddenException when attempting to modify another user's profile
     * Example:
     * PUT /api/users/testUser
     * Request Body:
     * {
     *   "age":24,
     *   "gender":"female",
     *   "height":170,
     *   "currentWeight":51
     * }
     */
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

    /**
     * Calculate and return BMI result for target user
     *
     * @param username target user identifier from url path
     * @param authentication spring security authentication holding logged-in username
     * @return computed BMIResponse with numeric value and category label
     * @throws ForbiddenException when accessing another user's BMI resource
     * Example: GET /api/users/testUser/bmi
     */
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