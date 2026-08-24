package com.calorie.service;

import com.calorie.exception.ResourceNotFoundException;
import com.calorie.model.FoodItem;
import com.calorie.model.FoodLog;
import com.calorie.repository.FoodLogRepository;
import com.calorie.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service handling FoodLog CRUD operations
 * Manages add/update/delete food entries inside FoodLog document
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodLogService {
    private final FoodLogRepository foodLogRepository;
    private final FoodService foodService;

    /**
     * Retrieve or create empty daily FoodLog for given username and date
     *
     * @param username target username
     * @param date local date in New York timezone
     * @return Optional FoodLog if exist, empty otherwise
     */
    public Optional<FoodLog> getFoodLogByDate(String username, LocalDate date) {
        LocalDateTime[] range = DateTimeUtil.getUtcRange(date);
        LocalDateTime utcStart = range[0];
        LocalDateTime utcEnd = range[1];

        return foodLogRepository.findLogByUsernameAndEstDay(username, utcStart, utcEnd);
    }

    /**
     * Add a new food item to user's FoodLog
     * Creates empty log document if none exists for target date
     *
     * @param username target username
     * @param date local date in New York timezone
     * @param foodName food name
     * @param calorie food calorie
     * @param note user-provided personal note for this food entry
     * @return FoodLog after new entry is added
     */
    public FoodLog addFoodEntry(String username, LocalDate date, String foodName, Integer calorie, String note) {
        LocalDateTime[] range = DateTimeUtil.getUtcRange(date);
        LocalDateTime utcStart = range[0];
        LocalDateTime utcEnd = range[1];

        FoodLog foodLog = foodLogRepository.findLogByUsernameAndEstDay(username, utcStart, utcEnd)
                .orElseGet(() -> createEmptyFoodLog(username, date));

        FoodItem foodItem = FoodItem.builder()
                .name(foodName)
                .calorie(calorie)
                .note(note)
                .build();

        foodLog.getFoods().add(foodItem);
        foodService.getOrCreateFood(foodName, calorie);

        return foodLogRepository.save(foodLog);
    }

    /**
     * Update an existing food entry by its index inside FoodLog
     * Invokes getOrCreateFood to refresh food template expiration timestamp
     *
     * @param username target username
     * @param date local date in New York timezone
     * @param index index of target food entry inside food list
     * @param foodName updated food name
     * @param calorie updated food calorie
     * @param note updated user personal note
     * @return FoodLog after update
     */
    public FoodLog updateFoodEntry(String username, LocalDate date, Integer index, String foodName, Integer calorie, String note) {
        LocalDateTime[] range = DateTimeUtil.getUtcRange(date);
        LocalDateTime utcStart = range[0];
        LocalDateTime utcEnd = range[1];

        FoodLog foodLog = foodLogRepository.findLogByUsernameAndEstDay(username, utcStart, utcEnd)
                .orElseThrow(() -> new ResourceNotFoundException("Food log not found for this date"));

        if (index < 0 || index >= foodLog.getFoods().size()) {
            throw new ResourceNotFoundException("Food entry not found");
        }

        FoodItem foodItem = foodLog.getFoods().get(index);
        foodItem.setName(foodName);
        foodItem.setCalorie(calorie);
        foodItem.setNote(note);

        foodService.getOrCreateFood(foodName, calorie);
        return foodLogRepository.save(foodLog);
    }

    /**
     * Remove specific food entry by its index inside FoodLog
     *
     * @param username target username
     * @param date local date in New York timezone
     * @param index index of target food entry inside food list
     * @return FoodLog after removal
     */
    public FoodLog removeFoodEntry(String username, LocalDate date, Integer index) {
        LocalDateTime[] range = DateTimeUtil.getUtcRange(date);
        LocalDateTime utcStart = range[0];
        LocalDateTime utcEnd = range[1];

        FoodLog foodLog = foodLogRepository.findLogByUsernameAndEstDay(username, utcStart, utcEnd)
                .orElseThrow(() -> new ResourceNotFoundException("Food log not found"));

        if (index < 0 || index >= foodLog.getFoods().size()) {
            throw new ResourceNotFoundException("Food entry not found");
        }

        foodLog.getFoods().remove((int) index);
        FoodLog savedFoodLog = foodLogRepository.save(foodLog);
        log.info("Food entry deleted for username: {} date: {}", username, date);
        return savedFoodLog;
    }

    /**
     * Sum total consumed calories for a given date
     *
     * @param username target username
     * @param date target date in New York timezone
     * @return sum of calories consumed that day, 0 if log absent
     */
    public Integer getTotalCaloriesForDate(String username, LocalDate date) {
        LocalDateTime[] range = DateTimeUtil.getUtcRange(date);
        LocalDateTime utcStart = range[0];
        LocalDateTime utcEnd = range[1];

        return foodLogRepository.findLogByUsernameAndEstDay(username, utcStart, utcEnd)
                .map(foodLog -> foodLog.getFoods().stream()
                        .mapToInt(FoodItem::getCalorie)
                        .sum())
                .orElse(0);
    }

    /**
     * Query multiple FoodLogs within inclusive date range
     * Converts New York local start/end dates into UTC timestamps for MongoDB query
     *
     * @param username target username
     * @param startDate inclusive start local date (America/New_York)
     * @param endDate inclusive end local date (America/New_York)
     * @return list of matched FoodLog
     */
    public List<FoodLog> getFoodLogsForPeriod(String username, LocalDate startDate, LocalDate endDate) {
        ZonedDateTime nyStart = startDate.atStartOfDay(ZoneId.of("America/New_York"));
        ZonedDateTime nyEndPlusOne = endDate.plusDays(1).atStartOfDay(ZoneId.of("America/New_York"));

        LocalDateTime utcStart = nyStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime utcEnd = nyEndPlusOne.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        return foodLogRepository.findLogsInEstRange(username, utcStart, utcEnd);
    }

    /**
     * Helper method: construct empty FoodLog for specific local New York date
     * Converts New York midnight timestamp to UTC for storage
     *
     * @param username target username
     * @param date local date in New York timezone
     * @return
     */
    private FoodLog createEmptyFoodLog(String username, LocalDate date) {
        ZonedDateTime nyMidnight = date.atStartOfDay(ZoneId.of("America/New_York"));
        LocalDateTime utcDateTime = nyMidnight.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        return FoodLog.builder()
                .username(username)
                .date(utcDateTime)
                .build();
    }
}