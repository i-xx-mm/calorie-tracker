package com.calorie.controller;

import com.calorie.dto.DashboardDto;
import com.calorie.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/today")
    public ResponseEntity<DashboardDto> getTodayDashboard(Authentication authentication) {
        String username = authentication.getName();
        DashboardDto dashboard = dashboardService.getTodayDashboard(username);

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/monthly-stats")
    public ResponseEntity<Map<String, Object>> getMonthlyStats(
            @RequestParam(defaultValue = "1") Integer months,
            Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> stats = dashboardService.getMonthlyStats(username, months);

        return ResponseEntity.ok(stats);
    }

}