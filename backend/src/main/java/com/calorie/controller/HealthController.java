package com.calorie.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Application health check endpoint.
 * Used for service liveness probe, no authentication required
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Simple backend liveness check
     * @return plain text success message
     * Example: GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend is running");
    }
}