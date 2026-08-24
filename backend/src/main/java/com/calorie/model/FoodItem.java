package com.calorie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded POJO stored inside FoodLog
 * Represents single consumed food entry inside one daily FoodLog record
 * Not mapped to MongoDB collection
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {
    /**
     * Local unique id for this item inside FoodLog entry
     */
    private String id;

    /**
     * Food item display name
     */
    private String name;

    /**
     * Calorie value of the food item, unit: kcal
     */
    private Integer calorie;

    /**
     * User-supplied personal note for this food item
     * May contain portion description, personal tasting, etc.
     * This note belongs only to embedded FoodItem inside FoodLog, independent of Food template document
     */
    private String note;
}