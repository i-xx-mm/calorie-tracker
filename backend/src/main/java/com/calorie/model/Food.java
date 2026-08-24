package com.calorie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB document for Food
 * Auto-cleanup by TTL(Time-to-Live) index when expireAt timestamp passes
 * Compound unique index prevents duplicate Food with identical name and calorie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "foods")
@CompoundIndex(name = "name_calorie_index", def = "{'name': 1, 'calorie': 1}", unique = true)
public class Food {
    /**
     * MongoDB document unique identifier
     */
    @Id
    private String id;

    /**
     * Normalized lowercase food name
     */
    private String name;

    /**
     * Calorie value of this food, unit: kcal
     */
    private Integer calorie;

    /**
     * TTL index: expire document AT this stored timestamp.
     * Application updates expireAt on each food usage to implement "30-day inactivity"
     * Document will be removed by MongoDB after this datetime
     */
    @Indexed
    private LocalDateTime expireAt;
}