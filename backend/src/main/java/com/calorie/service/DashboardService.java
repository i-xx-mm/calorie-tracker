package com.calorie.service;

import com.calorie.dto.BMIResponse;
import com.calorie.dto.DashboardDto;
import com.calorie.dto.DashboardDto.BMIInfoDto;
import com.calorie.dto.DashboardDto.CalorieTrackingDto;
import com.calorie.model.FoodLog;
import com.calorie.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Service for assembling dashboard view data and monthly calorie statistics
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final FoodLogService foodLogService;
    private final UserService userService;

    /**
     * Build today's dashboard snapshot for given user
     * Computes consumed/remaining/percentage calorie metrics, BMI info, and count of logged food items
     * Food count defaults to 0 when no FoodLog found
     *
     * @param username target username
     * @return DashboardDto containing calorie tracking, bmi, and log metadata
     */
    public DashboardDto getTodayDashboard(String username) {
        LocalDate today = DateTimeUtil.getTodayEST();
        Integer consumed = foodLogService.getTotalCaloriesForDate(username, today);

        Integer suggestedDaily = userService.getGoalCalories(username);
        Integer remaining = suggestedDaily - consumed;
        double percentage = suggestedDaily == 0 ? 0d : (consumed * 100.0) / suggestedDaily;

        DashboardDto.CalorieTrackingDto calorieTracking = DashboardDto.CalorieTrackingDto.builder()
                .consumed(consumed)
                .suggestedDaily(suggestedDaily)
                .remaining(remaining)
                .percentage(percentage)
                .build();

        BMIResponse bmi = userService.calculateBMI(username);
        DashboardDto.BMIInfoDto bmiInfo = DashboardDto.BMIInfoDto.builder()
                .value(bmi.getBmi())
                .category(bmi.getCategory())
                .status(bmi.getStatus())
                .build();

        Optional<FoodLog> optionalFoodLog = foodLogService.getFoodLogByDate(username, today);
        Map<String, Integer> caloriesByMeal = new HashMap<>();

        return DashboardDto.builder()
                .username(username)
                .date(today)
                .calorieTracking(calorieTracking)
                .bmi(bmiInfo)
                .foodsLogged(optionalFoodLog.map(log ->log.getFoods().size()).orElse(0))
                .caloriesByMeal(caloriesByMeal)
                .build();
    }

    /**
     * Calculate monthly aggregated calorie statistics for a given trailing month count
     * Computes daily calorie totals, average consumption, highest/lowest calorie day
     * Timestamps are converted from UTC to EST
     *
     * @param username target username
     * @param months number of past months to look back from today (EST)
     * @return map response containing date-series dailyData and computed summary metrics
     * {
     *   "username": "[user-name]",
     *   "period": "[start-date] to [end-date]",
     *   "dailyData": [
     *       {"date": "[local-date-string]", "totalCalories": [integer]}
     *   ],
     *   "summary": {
     *       "averageDailyConsumption": [integer],
     *       "highestDay": "[local-date|null]",
     *       "highestDayCalories": [integer],
     *       "lowestDay": "[local-date|null]",
     *       "lowestDayCalories": [integer],
     *       "daysWithLogs": [integer],
     *       "totalLogged": [integer]
     *   }
     * }
     */
    public Map<String, Object> getMonthlyStats(String username, Integer months) {
        LocalDate endDate = DateTimeUtil.getTodayEST();
        LocalDate startDate = endDate.minusMonths(months);

        List<FoodLog> foodLogs = foodLogService.getFoodLogsForPeriod(username, startDate, endDate);

        Map<String, Integer> dailyTotals = new HashMap<>();
        Integer maxCalories = 0;
        Integer minCalories = Integer.MAX_VALUE;
        LocalDate maxDay = null;
        LocalDate minDay = null;

        for (FoodLog log : foodLogs) {
            Integer total = log.getFoods().stream()
                    .mapToInt(food -> food.getCalorie() != null ? food.getCalorie() : 0)
                    .sum();

            ZonedDateTime utcTime = log.getDate().atZone(ZoneId.of("UTC"));
            ZonedDateTime nyTime = utcTime.withZoneSameInstant(ZoneId.of("America/New_York"));
            LocalDate logNyDay = nyTime.toLocalDate();
            String dayKey = logNyDay.toString();

            dailyTotals.put(dayKey, total);

            if (total > maxCalories) {
                maxCalories = total;
                maxDay = logNyDay;
            }
            if (total < minCalories) {
                minCalories = total;
                minDay = logNyDay;
            }
        }

        List<Map<String, Object>> dailyDataList = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : dailyTotals.entrySet()){
            Map<String,Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("totalCalories", entry.getValue());
            dailyDataList.add(item);
        }

        dailyDataList.sort( (a,b) -> {
            String dateA = (String) a.get("date");
            String dateB = (String) b.get("date");
            return dateA.compareTo(dateB);
        });

        int sumCal = dailyTotals.values().stream().mapToInt(Integer::intValue).sum();
        int averageDaily = dailyTotals.isEmpty() ? 0 : sumCal / Math.max(dailyTotals.size(), 1);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("period", startDate + " to " + endDate);
        response.put("dailyData", dailyDataList);

        Map<String, Object> summary = new HashMap<>();
        summary.put("averageDailyConsumption", averageDaily);
        summary.put("highestDay", maxDay);
        summary.put("highestDayCalories", maxCalories);
        summary.put("lowestDay", minDay);
        summary.put("lowestDayCalories", minCalories == Integer.MAX_VALUE ? 0 : minCalories);
        summary.put("daysWithLogs", dailyTotals.size());
        summary.put("totalLogged", sumCal);

        response.put("summary", summary);
        return response;
    }
}