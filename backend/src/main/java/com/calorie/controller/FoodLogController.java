package com.calorie.controller;

import com.calorie.dto.AddFoodLogRequest;
import com.calorie.dto.FoodLogDto;
import com.calorie.model.FoodLog;
import com.calorie.service.FoodLogService;
import com.calorie.util.DateTimeUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/foodlogs")
@RequiredArgsConstructor
public class FoodLogController {
    private final FoodLogService foodLogService;

    @GetMapping
    public ResponseEntity<FoodLogDto> getFoodLog(
            @RequestParam(required = false) String date,
            Authentication authentication) {
        String username = authentication.getName();
        LocalDate logDate = date != null ? LocalDate.parse(date) : DateTimeUtil.getTodayEST();
        Optional<FoodLog> optionalFoodLog = foodLogService.getFoodLogByDate(username, logDate);
        Integer totalCalories = foodLogService.getTotalCaloriesForDate(username, logDate);
        FoodLogDto dto;
        if (optionalFoodLog.isPresent()) {
            FoodLog foodLog = optionalFoodLog.get();
            dto = FoodLogDto.builder()
                    .id(foodLog.getId())
                    .username(foodLog.getUsername())
                    .date(DateTimeUtil.utcLocalDateTimeToEstLocalDate(foodLog.getDate()))
                    .foods(foodLog.getFoods())
                    .totalCalories(totalCalories)
                    .build();

        } else {
            dto = FoodLogDto.builder()
                    .username(username)
                    .date(logDate)
                    .foods(List.of())
                    .totalCalories(totalCalories)
                    .build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<FoodLogDto> addFoodEntry(
            @Valid @RequestBody AddFoodLogRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        LocalDate date = request.getDate() != null ? LocalDate.parse(request.getDate()) : DateTimeUtil.getTodayEST();

        FoodLog foodLog = foodLogService.addFoodEntry(username, date, request.getFoodName(), request.getCalorie(), request.getNote());
        Integer totalCalories = foodLogService.getTotalCaloriesForDate(username, date);

        FoodLogDto dto = FoodLogDto.builder()
                .id(foodLog.getId())
                .username(foodLog.getUsername())
                .date(DateTimeUtil.utcLocalDateTimeToEstLocalDate(foodLog.getDate()))
                .foods(foodLog.getFoods())
                .totalCalories(totalCalories)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @PutMapping("/{foodLogId}")
    public ResponseEntity<FoodLogDto> updateFoodEntry(
            @PathVariable String foodLogId,
            @RequestParam Integer index,
            @Valid @RequestBody AddFoodLogRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        LocalDate date = request.getDate() != null ? LocalDate.parse(request.getDate()) : DateTimeUtil.getTodayEST();

        FoodLog foodLog = foodLogService.updateFoodEntry(username, date, index, request.getFoodName(), request.getCalorie(), request.getNote());
        Integer totalCalories = foodLogService.getTotalCaloriesForDate(username, date);

        FoodLogDto dto = FoodLogDto.builder()
                .id(foodLog.getId())
                .username(foodLog.getUsername())
                .date(DateTimeUtil.utcLocalDateTimeToEstLocalDate(foodLog.getDate()))
                .foods(foodLog.getFoods())
                .totalCalories(totalCalories)
                .build();

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{foodLogId}")
    public ResponseEntity<FoodLogDto> deleteFoodEntry(
            @PathVariable String foodLogId,
            @RequestParam Integer index,
            Authentication authentication) {
        String username = authentication.getName();
        LocalDate date = DateTimeUtil.getTodayEST();

        FoodLog foodLog = foodLogService.removeFoodEntry(username, date, index);
        Integer totalCalories = foodLogService.getTotalCaloriesForDate(username, date);

        FoodLogDto dto = FoodLogDto.builder()
                .id(foodLog.getId())
                .username(foodLog.getUsername())
                .date(DateTimeUtil.utcLocalDateTimeToEstLocalDate(foodLog.getDate()))
                .foods(foodLog.getFoods())
                .totalCalories(totalCalories)
                .build();

        return ResponseEntity.ok(dto);
    }
}