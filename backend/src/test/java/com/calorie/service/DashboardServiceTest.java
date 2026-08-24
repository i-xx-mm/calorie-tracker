package com.calorie.service;

import com.calorie.dto.BMIResponse;
import com.calorie.dto.DashboardDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
    @Mock
    private FoodLogService foodLogService;
    @Mock
    private UserService userService;
    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getTodayDashboard_calculateCorrectPercentage() {
        when(foodLogService.getTotalCaloriesForDate(anyString(), any(LocalDate.class))).thenReturn(900);
        when(userService.getGoalCalories("test1")).thenReturn(1800);

        BMIResponse bmiResp = new BMIResponse();
        bmiResp.setBmi(20.2);
        bmiResp.setCategory("Normal Weight");
        bmiResp.setStatus("Healthy");
        when(userService.calculateBMI("test1")).thenReturn(bmiResp);

        when(foodLogService.getFoodLogByDate(anyString(), any(LocalDate.class)))
                .thenReturn(Optional.of(mockLog()));

        DashboardDto dto = dashboardService.getTodayDashboard("test1");
        assertEquals(900, dto.getCalorieTracking().getConsumed());
        assertEquals(1800, dto.getCalorieTracking().getSuggestedDaily());
        assertEquals(50, dto.getCalorieTracking().getPercentage());
    }

    @Test
    void getMonthlyStats_emptyLog_returnZeroAverage() {
        when(foodLogService.getFoodLogsForPeriod(anyString(),any(),any())).thenReturn(Collections.emptyList());
        var out = dashboardService.getMonthlyStats("test1",1);
        var summary = (java.util.Map<?,?>) out.get("summary");
        assertEquals(0, summary.get("averageDailyConsumption"));
    }

    private com.calorie.model.FoodLog mockLog(){
        com.calorie.model.FoodLog log = new com.calorie.model.FoodLog();
        log.setFoods(Collections.emptyList());
        return log;
    }
}