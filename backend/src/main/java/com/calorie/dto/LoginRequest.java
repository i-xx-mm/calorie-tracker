package com.calorie.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Login request DTO for user authentication endpoint
 * Accepts raw username and plain‑text password from client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    /**
     * User login username, cannot be blank
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * Raw plain‑text password transmitted from client, will be Bcrypt‑hashed for verification
     */
    @NotBlank(message = "Password is required")
    private String password;
}