package com.calorie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFoodLogRequest {
    @NotBlank(message = "Food name is required")
    private String foodName;

    @Min(value = 0, message = "Calorie must be at least 0")
    private Integer calorie;

    private String note;
    private String date;
}