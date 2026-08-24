package com.calorie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Request DTO for adding new food entry into daily FoodLog
 * Carries client input for one consumed food record
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFoodLogRequest {
    /**
     * Name of consumed food item, cannot be blank.
     */
    @NotBlank(message = "Food name is required")
    private String foodName;

    /**
     * Calories for this food entry, must be greater or equal to zero.
     */
    @Min(value = 0, message = "Calorie must be at least 0")
    private Integer calorie;

    /**
     * Optional personal note for this log entry, stored inside FoodItem embedded document.
     * Independent of global Food document.
     */
    private String note;

    /**
     * Target log date string for this food entry
     * Expected date format resolves to LocalDate.
     */
    private String date;
}