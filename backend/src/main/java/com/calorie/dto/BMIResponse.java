package com.calorie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BMIResponse {
    private String username;
    private Integer height;
    private Double weight;
    private Double bmi;
    private String category;
    private String status;
    private LocalDateTime calculatedAt;
}