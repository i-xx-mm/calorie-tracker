package com.calorie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB document for FoodLog - one-day calorie intake log for a user
 * Compound unique index ensures only one log exists per user per calendar day
 * Contains embedded list of FoodItem consumed on that day
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "foodlogs")
@CompoundIndex(name = "username_date_index", def = "{'username': 1, 'date': 1}", unique = true)
public class FoodLog {
    /**
     * MongoDB document unique identifier
     */
    @Id
    private String id;

    /**
     * Associated username
     */
    private String username;

    /**
     * UTC timestamp representing target EST calendar day for this FoodLog
     */
    private LocalDateTime date;

    /**
     * List of FoodItem consumed on this day
     * Initialized as empty ArrayList by default
     */
    @Builder.Default
    private List<FoodItem> foods = new ArrayList<>();
}