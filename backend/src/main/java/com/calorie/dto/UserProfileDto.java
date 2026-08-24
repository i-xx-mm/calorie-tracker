package com.calorie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * DTO returning complete user profile information to frontend
 * goalCalories is server-side computed field; client incoming value will be ignored
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    /**
     * User login username
     */
    private String username;

    /**
     * User full‑year age. Valid range:13-120, enforced at registration per COPPA compliance.
     */
    @Min(value = 13, message = "Age must be between 13 and 120")
    @Max(value = 120)
    private Integer age;

    /**
     * User height measured in cm, valid range: 50-300 cm.
     */
    @Min(value = 50, message = "Height must be between 50 and 300 cm")
    @Max(value = 300)
    private Integer height;

    /**
     * User gender value, supports male/female/other
     */
    private String gender;

    /**
     * Current user weight in kg, valid range:20-500 kg
     */
    @Min(value = 20, message = "Weight must be between 20 and 500 kg")
    @Max(value = 500)
    private Double currentWeight;

    /**
     * Timestamp when user account profile was first created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when user profile was last modified
     */
    private LocalDateTime updatedAt;

    /**
     * Calculated daily calorie goal (TDEE) based on user's metrics
     * Uses Mifflin-St Jeor formula: BMR × 1.55 (moderate activity level)
     * Falls back to 2000 kcal if required physical metrics are incomplete
     * Field is read‑only on API: client‑supplied input will be discarded by setGoalCalories()
     */
    private Integer goalCalories;

    /**
     * Jackson setter override, ignores goalCalories payload sent from client request body
     * Prevents end-user overriding computed server-side calorie target
     * @param goalCalories incoming client value, will not be assigned
     */
    @JsonSetter
    public void setGoalCalories(Integer goalCalories) {
        // do nothing, ignore incoming value from client
    }
}