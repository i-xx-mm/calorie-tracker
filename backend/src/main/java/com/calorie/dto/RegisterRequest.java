package com.calorie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height must not exceed 300 cm")
    private Integer height; // in cm

    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 500, message = "Weight must not exceed 500 kg")
    private Integer weight; // in kg

    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 120, message = "Age must not exceed 120")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender; // male, female, other
}