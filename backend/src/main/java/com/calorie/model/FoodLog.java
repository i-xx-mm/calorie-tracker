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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "foodlogs")
@CompoundIndex(name = "username_date_index", def = "{'username': 1, 'date': 1}", unique = true)
public class FoodLog {
    @Id
    private String id;

    private String username;

    private LocalDateTime date;

    @Builder.Default
    private List<FoodItem> foods = new ArrayList<>();
}