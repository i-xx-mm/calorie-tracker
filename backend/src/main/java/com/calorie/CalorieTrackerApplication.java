package com.calorie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for Calorie Tracker backend Spring Boot application
 */
@SpringBootApplication
public class CalorieTrackerApplication {

    /**
     * Application main method, bootstraps the Spring Boot runtime
     * @param args command‑line arguments passed during application startup
     */
    public static void main(String[] args) {
        SpringApplication.run(CalorieTrackerApplication.class, args);
    }

}
