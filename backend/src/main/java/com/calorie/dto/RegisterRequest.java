package com.calorie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * User registration HTTP request DTO
 * Input validation enforced at API layer following COPPA and reasonable physical metric bounds
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    /**
     * Unique account login username, cannot be blank
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * Raw plain‑text password from client, backend will store Bcrypt hash only
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * User height in cm. Valid range: 50-300 cm
     */
    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height must not exceed 300 cm")
    private Integer height;

    /**
     * User weight in kg. Valid range: 20-500 kg
     */
    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 500, message = "Weight must not exceed 500 kg")
    private Integer weight;

    /**
     * User age in full years. Minimum value 13 enforced per COPPA compliance requirement
     */
    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 120, message = "Age must not exceed 120")
    private Integer age;

    /**
     * User gender. Accepts "male", "female", "other".
     * Non‑male/female inputs fall back to default calorie value inside Mifflin‑St Jeor calculation.
     */
    @NotBlank(message = "Gender is required")
    private String gender;
}