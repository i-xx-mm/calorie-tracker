package com.calorie.service;

import com.calorie.exception.ResourceNotFoundException;
import com.calorie.model.Food;
import com.calorie.model.FoodItem;
import com.calorie.model.FoodLog;
import com.calorie.repository.FoodLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodLogServiceTest {
    @Mock
    private FoodLogRepository foodLogRepository;
    @Mock
    private FoodService foodService;
    @InjectMocks
    private FoodLogService foodLogService;

    @Test
    void getFoodLogByDate_notExist_createEmpty() {
        LocalDate testDay = LocalDate.of(2026,8,20);
        when(foodLogRepository.findLogByUsernameAndEstDay(anyString(), any(), any())).thenReturn(Optional.empty());

        Optional<FoodLog> optionalFoodLog = foodLogService.getFoodLogByDate("test1", testDay);

        assertTrue(optionalFoodLog.isEmpty());
    }

    void getFoodLogByDate_exists_returnFoodLog() {
        LocalDate testDay = LocalDate.of(2026,8,20);
        FoodLog mockLog = new FoodLog();
        mockLog.setUsername("test1");

        when(foodLogRepository.findLogByUsernameAndEstDay(anyString(), any(), any()))
                .thenReturn(Optional.of(mockLog));

        Optional<FoodLog> optionalFoodLog = foodLogService.getFoodLogByDate("test1", testDay);

        assertTrue(optionalFoodLog.isPresent());
        assertEquals("test1", optionalFoodLog.get().getUsername());
    }

    @Test
    void addFoodEntry_existingLog_appendItem() {
        LocalDate day = LocalDate.of(2026,8,20);
        FoodLog existLog = new FoodLog();
        existLog.setUsername("test1");
        existLog.setFoods(new ArrayList<>());

        when(foodLogRepository.findLogByUsernameAndEstDay(anyString(),any(),any())).thenReturn(Optional.of(existLog));
        when(foodLogRepository.save(any())).thenReturn(existLog);

        FoodLog result = foodLogService.addFoodEntry("test1", day, "apple",95,"snack");
        assertEquals(1, result.getFoods().size());
        verify(foodService).getOrCreateFood("apple",95);
    }

    @Test
    void removeFoodEntry_wrongIndex_throw() {
        LocalDate day = LocalDate.of(2026,8,20);
        FoodLog log = new FoodLog();
        log.setUsername("test1");
        log.setFoods(new ArrayList<>());

        when(foodLogRepository.findLogByUsernameAndEstDay(anyString(),any(),any())).thenReturn(Optional.of(log));
        assertThrows(ResourceNotFoundException.class, ()-> foodLogService.removeFoodEntry("test1", day, 0));
    }

    @Test
    void getTotalCaloriesForDate_hasEntries_sumCorrect() {
        LocalDate day = LocalDate.of(2026,8,20);
        FoodLog log = new FoodLog();
        log.setUsername("test1");
        log.setFoods(new ArrayList<>());

        FoodItem item1 = new FoodItem();
        item1.setCalorie(100);
        FoodItem item2 = new FoodItem();
        item2.setCalorie(200);
        log.getFoods().add(item1);
        log.getFoods().add(item2);

        when(foodLogRepository.findLogByUsernameAndEstDay(anyString(),any(),any())).thenReturn(Optional.of(log));
        Integer total = foodLogService.getTotalCaloriesForDate("test1", day);
        assertEquals(300, total);
    }

    @Test
    void getTotalCaloriesForDate_noLog_returnZero() {
        LocalDate day = LocalDate.of(2026,8,20);
        when(foodLogRepository.findLogByUsernameAndEstDay(anyString(),any(),any())).thenReturn(Optional.empty());
        Integer total = foodLogService.getTotalCaloriesForDate("test1", day);
        assertEquals(0, total);
    }
}