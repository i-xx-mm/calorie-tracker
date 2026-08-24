package com.calorie.dto;

import com.calorie.model.FoodItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
/**
 * DTO representing a single day FoodLog record returned to frontend
 * Contains embedded list of consumed FoodItem entries and total calorie sum
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodLogDto {
    /**
     * MongoDB document id of FoodLog entry
     */
    private String id;

    /**
     * Owner username of this food log
     */
    private String username;

    /**
     * Log entry target calendar date.
     */
    private LocalDate date;

    /**
     * List of FoodItems consumed on this day
     * Each item may carry personal note
     */
    private List<FoodItem> foods;

    /**
     * Sum total calories of all food items within this daily log
     */
    private Integer totalCalories;
}