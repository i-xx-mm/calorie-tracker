package com.calorie.dto;

import com.calorie.model.FoodItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodLogDto {
    private String id;
    private String username;
    private LocalDate date;
    private List<FoodItem> foods;
    private Integer totalCalories;
}