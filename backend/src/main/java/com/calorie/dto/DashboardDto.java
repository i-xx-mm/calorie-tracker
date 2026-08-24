package com.calorie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
/**
 * Aggregated dashboard DTO for frontend dashboard view
 * Combines calorie tracking summary and BMI information for given user and date
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    /**
     * Username of dashboard owner
     */
    private String username;

    /**
     * Target date for calorie log aggregation
     */
    private LocalDate date;

    /**
     * Daily calorie consumption and goal summary
     */
    private CalorieTrackingDto calorieTracking;

    /**
     * User BMI computed metrics
     */
    private BMIInfoDto bmi;

    /**
     * Total count of food items logged on selected day
     */
    private Integer foodsLogged;

    /**
     * Nested DTO holding daily calorie tracking metrics.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalorieTrackingDto {
        /**
         * Total calories consumed for target day
         */
        private Integer consumed;

        /**
         * Suggested daily calorie goal computed from Mifflin‑St Jeor formula
         */
        private Integer suggestedDaily;

        /**
         * Remaining allowed calories for the day
         */
        private Integer remaining;

        /**
         * Percentage of daily calorie goal already consumed
         */
        private Double percentage;
    }

    /**
     * Nested DTO for lightweight BMI summary shown on dashboard
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BMIInfoDto {
        /**
         * Calculated BMI numeric value.
         */
        private Double value;

        /**
         * BMI classification category.
         */
        private String category;

        /**
         * Computation status flag
         */
        private String status;
    }
}