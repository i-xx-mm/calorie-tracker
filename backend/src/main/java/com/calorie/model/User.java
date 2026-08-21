package com.calorie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private Integer age;

    private Integer height; // in cm

    private String gender; // M or F

    private Double currentWeight; // in kg

    private LocalDateTime createdAt;

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Calculate daily calorie goal (TDEE) using Mifflin‑St Jeor formula
     * Formula: BMR × Activity Level (1.55 for moderate activity)
     * Returns 2000 if height, weight, age, or gender not set
     */
    public Integer calculateGoalCalories() {
        if (height == null || currentWeight == null || age == null || gender == null) {
            // standard adult daily calorie recommendation
            return 2000;
        }
        // Mifflin‑St Jeor formula for BMR
        double bmr;
        if ("male".equalsIgnoreCase(gender)) {
            bmr = 10 * currentWeight + 6.25 * height - 5 * age + 5;
        } else if ("female".equalsIgnoreCase(gender)) {
            bmr = 10 * currentWeight + 6.25 * height - 5 * age - 161;
        } else {
            // gender = other
            return 2000;
        }
        // TDEE = BMR × Activity Level (1.55 = moderate activity)
        int tdee = Math.round((float) (bmr * 1.55));
        return Math.max(tdee, 1200); // Minimum 1200 kcal for safety
    }
}