package com.calorie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * Response DTO carrying calculated BMI result for user profile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BMIResponse {
    /**
     * Target user username.
     */
    private String username;

    /**
     * User height measured in centimeters.
     */
    private Integer height;

    /**
     * User weight measured in kilograms.
     */
    private Double weight;

    /**
     * Computed BMI numeric value.
     */
    private Double bmi;

    /**
     * Human‑readable BMI category
     * e.g. Normal, Overweight, Underweight, Obese.
     */
    private String category;

    /**
     * Timestamp when BMI calculation was performed on backend.
     */
    private LocalDateTime calculatedAt;
}