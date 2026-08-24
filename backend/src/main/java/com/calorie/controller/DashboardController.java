package com.calorie.controller;

import com.calorie.dto.DashboardDto;
import com.calorie.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Dashboard REST controller
 * Provides aggregated calorie summary, BMI, and historical monthly statistics for authenticated user
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    /**
     * Get aggregated dashboard view for today (EST timezone)
     * Contains consumed/remaining calories, BMI summary, and food log count
     *
     * @param authentication Spring security authentication holding logged-in username
     * @return aggregated DashboardDto for frontend rendering
     * Example: GET /api/dashboard/today
     */
    @GetMapping("/today")
    public ResponseEntity<DashboardDto> getTodayDashboard(Authentication authentication) {
        String username = authentication.getName();
        DashboardDto dashboard = dashboardService.getTodayDashboard(username);

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get aggregated historical calorie statistics for recent months
     *
     * @param months number of past months to calculate statistics for, default = 1
     * @param authentication Spring security authentication holding logged-in username
     * @return untyped map of aggregated historical metrics
     * Example: GET /api/dashboard/monthly-stats?months=3
     */
    @GetMapping("/monthly-stats")
    public ResponseEntity<Map<String, Object>> getMonthlyStats(
            @RequestParam(defaultValue = "1") Integer months,
            Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> stats = dashboardService.getMonthlyStats(username, months);

        return ResponseEntity.ok(stats);
    }

}