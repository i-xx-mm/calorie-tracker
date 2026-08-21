package com.calorie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    private String username;
    private LocalDate date;
    private CalorieTrackingDto calorieTracking;
    private BMIInfoDto bmi;
    private Integer foodsLogged;
    private Map<String, Integer> caloriesByMeal;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalorieTrackingDto {
        private Integer consumed;
        private Integer suggestedDaily;
        private Integer remaining;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BMIInfoDto {
        private Double value;
        private String category;
        private String status;
    }
}