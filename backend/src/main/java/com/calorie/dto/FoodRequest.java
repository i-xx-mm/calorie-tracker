package com.calorie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRequest {
    @NotBlank(message = "Food name is required")
    private String name;

    @Min(value = 1, message = "Calorie must be at least 1")
    @Max(value = 10000, message = "Calorie cannot exceed 10000")
    private Integer calorie;

    private String note; // Optional note about the food
}