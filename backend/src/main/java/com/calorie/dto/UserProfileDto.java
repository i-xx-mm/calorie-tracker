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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private String username;

    @Min(value = 1, message = "Age must be between 1 and 150")
    @Max(value = 150)
    private Integer age;

    @Min(value = 50, message = "Height must be between 50 and 300 cm")
    @Max(value = 300)
    private Integer height;

    private String gender;

    @Min(value = 20, message = "Weight must be between 20 and 500 kg")
    @Max(value = 500)
    private Double currentWeight;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Calculated daily calorie goal (TDEE) based on user's metrics
     * Uses Mifflin‑St Jeor formula: BMR × 1.55 (moderate activity level)
     * 2000 if height, weight, age, or gender not set
     */
    private Integer goalCalories;

    @JsonSetter
    public void setGoalCalories(Integer goalCalories) {
        // do nothing, ignore incoming value from client
    }
}