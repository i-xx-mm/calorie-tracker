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

/**
 * FoodLog REST controller
 * Create, read, update, delete operations for user daily food consumption records
 * All operations are user-scoped via authenticated username from JWT
 */
@RestController
@RequestMapping("/api/foodlogs")
@RequiredArgsConstructor
public class FoodLogController {
    private final FoodLogService foodLogService;

    /**
     * Fetch daily food log for target date. Uses today EST if date parameter omitted
     * Returns empty food list when no log document exists for given date
     *
     * @param date ISO local date string, optional
     * @param authentication spring security authentication holding logged‑in username
     * @return FoodLogDto containing food item list and total calorie
     * Example: GET /api/foodlogs?date=2026-08-22
     */
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

    /**
     * Append one food entry to daily food log
     * Triggers getOrCreateFood to maintain cached Food collection
     *
     * @param request validated payload containing foodName, calorie, note and target date
     * @param authentication Spring security authentication holding logged‑in username
     * @return updated FoodLogDto with 201 Created status
     * Example:
     * POST /api/foodlogs
     * Request Body:
     * {
     *   "foodName":"oatmeal",
     *   "calorie":280,
     *   "note":"with milk",
     *   "date":"2026-08-22"
     * }
     */
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

    /**
     * Modify an existing food entry inside daily log by list index
     *
     * @param foodLogId MongoDB document id of target food log
     * @param index zero‑based position of FoodItem inside foods list to update
     * @param request updated food payload
     * @param authentication Spring security authentication holding logged‑in username
     * @return refreshed FoodLogDto after modification
     * Example:
     * PUT /api/foodlogs/66abc123def?index=0
     * Request Body:
     * {
     *   "foodName":"oatmeal",
     *   "calorie":320,
     *   "note":"extra blueberry",
     *   "date":"2026-08-22"
     * }
     */
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

    /**
     * Remove single food entry from log by list index
     * Operates against today's EST food‑log document
     *
     * @param foodLogId MongoDB document id of target food log
     * @param index zero‑based position of FoodItem to delete
     * @param authentication spring security authentication holding logged-in username
     * @return refreshed FoodLogDto after deletion
     * Example: DELETE /api/foodlogs/66abc123def?index=0
     */
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