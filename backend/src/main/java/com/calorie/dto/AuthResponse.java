package com.calorie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Response DTO returned after successful login or registration
 * Contains JWT access token and basic authenticated user metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    /**
     * JWT Bearer access token for subsequent authenticated API calls
     */
    private String token;

    /**
     * Authenticated user login username
     */
    private String username;

    /**
     * Token expiry time in milliseconds
     */
    private Long expiresIn;
}