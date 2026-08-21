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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "foods")
@CompoundIndex(name = "name_calorie_index", def = "{'name': 1, 'calorie': 1}", unique = true)
public class Food {
    @Id
    private String id;

    private String name;

    private Integer calorie;

    /**
     * TTL index: expire document AT this stored timestamp.
     * Application updates expireAt on each food usage to implement "30‑day inactivity".
     */
    @Indexed
    private LocalDateTime expireAt;
}