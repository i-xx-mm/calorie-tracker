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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final FoodLogService foodLogService;
    private final UserService userService;

    public DashboardDto getTodayDashboard(String username) {
        LocalDate today = DateTimeUtil.getTodayEST();
        Integer consumed = foodLogService.getTotalCaloriesForDate(username, today);

        Integer suggestedDaily = userService.getGoalCalories(username);
        Integer remaining = Math.max(0, suggestedDaily - consumed);
        Integer percentage = (consumed * 100) / suggestedDaily;

        DashboardDto.CalorieTrackingDto calorieTracking = DashboardDto.CalorieTrackingDto.builder()
                .consumed(consumed)
                .suggestedDaily(suggestedDaily)
                .remaining(remaining)
                .percentage(Math.min(percentage, 100))
                .build();

        BMIResponse bmi = userService.calculateBMI(username);
        DashboardDto.BMIInfoDto bmiInfo = DashboardDto.BMIInfoDto.builder()
                .value(bmi.getBmi())
                .category(bmi.getCategory())
                .status(bmi.getStatus())
                .build();

        FoodLog foodLog = foodLogService.getFoodLogByDate(username, today);
        Map<String, Integer> caloriesByMeal = new HashMap<>();

        return DashboardDto.builder()
                .username(username)
                .date(today)
                .calorieTracking(calorieTracking)
                .bmi(bmiInfo)
                .foodsLogged(foodLog.getFoods().size())
                .caloriesByMeal(caloriesByMeal)
                .build();
    }

    public Map<String, Object> getMonthlyStats(String username, Integer months) {
        LocalDate endDate = DateTimeUtil.getTodayEST();
        LocalDate startDate = endDate.minusMonths(months);

        List<FoodLog> foodLogs = foodLogService.getFoodLogsForPeriod(username, startDate, endDate);

        for(FoodLog log : foodLogs){
            System.out.println("log date(UTC): "+log.getDate() + " username:"+log.getUsername());
        }
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