package com.calorie.service;

import com.calorie.exception.ResourceNotFoundException;
import com.calorie.model.FoodItem;
import com.calorie.model.FoodLog;
import com.calorie.repository.FoodLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodLogService {
    private final FoodLogRepository foodLogRepository;
    private final FoodService foodService;

    public FoodLog getFoodLogByDate(String username, LocalDate date) {
        ZonedDateTime nyStart = date.atStartOfDay(ZoneId.of("America/New_York"));
        ZonedDateTime nyNextDay = date.plusDays(1).atStartOfDay(ZoneId.of("America/New_York"));

        LocalDateTime utcStart = nyStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime utcEnd = nyNextDay.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        return foodLogRepository.findLogByUsernameAndEstDay(username, utcStart, utcEnd)
                .orElseGet(() -> createEmptyFoodLog(username, date));
    }

    public FoodLog addFoodEntry(String username, LocalDate date, String foodName, Integer calorie, String note) {
        ZonedDateTime nyStart = date.atStartOfDay(ZoneId.of("America/New_York"));
        ZonedDateTime nyNextDay = date.plusDays(1).atStartOfDay(ZoneId.of("America/New_York"));

        LocalDateTime utcStart = nyStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime utcEnd = nyNextDay.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

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

    public FoodLog updateFoodEntry(String username, LocalDate date, Integer index, String foodName, Integer calorie, String note) {
        ZonedDateTime nyStart = date.atStartOfDay(ZoneId.of("America/New_York"));
        ZonedDateTime nyNextDay = date.plusDays(1).atStartOfDay(ZoneId.of("America/New_York"));

        LocalDateTime utcStart = nyStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime utcEnd = nyNextDay.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

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

    public FoodLog removeFoodEntry(String username, LocalDate date, Integer index) {
        ZonedDateTime nyStart = date.atStartOfDay(ZoneId.of("America/New_York"));
        ZonedDateTime nyNextDay = date.plusDays(1).atStartOfDay(ZoneId.of("America/New_York"));

        LocalDateTime utcStart = nyStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime utcEnd = nyNextDay.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

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

    public Integer getTotalCaloriesForDate(String username, LocalDate date) {
        ZonedDateTime nyStart = date.atStartOfDay(ZoneId.of("America/New_York"));
        ZonedDateTime nyNextDay = date.plusDays(1).atStartOfDay(ZoneId.of("America/New_York"));

        LocalDateTime utcStart = nyStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime utcEnd = nyNextDay.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        return foodLogRepository.findLogByUsernameAndEstDay(username, utcStart, utcEnd)
                .map(foodLog -> foodLog.getFoods().stream()
                        .mapToInt(FoodItem::getCalorie)
                        .sum())
                .orElse(0);
    }

    public List<FoodLog> getFoodLogsForPeriod(String username, LocalDate startDate, LocalDate endDate) {
        ZonedDateTime nyStart = startDate.atStartOfDay(ZoneId.of("America/New_York"));
        ZonedDateTime nyEndPlusOne = endDate.plusDays(1).atStartOfDay(ZoneId.of("America/New_York"));

        LocalDateTime utcStart = nyStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime utcEnd = nyEndPlusOne.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        return foodLogRepository.findLogsInEstRange(username, utcStart, utcEnd);
    }

    private FoodLog createEmptyFoodLog(String username, LocalDate date) {
        ZonedDateTime nyMidnight = date.atStartOfDay(ZoneId.of("America/New_York"));
        LocalDateTime utcDateTime = nyMidnight.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        return FoodLog.builder()
                .username(username)
                .date(utcDateTime)
                .build();
    }
}