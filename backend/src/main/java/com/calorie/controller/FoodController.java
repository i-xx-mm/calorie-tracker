package com.calorie.controller;

import com.calorie.model.Food;
import com.calorie.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Food REST controller
 * Provides search capability for auto-complete dropdown on frontend
 * Food documents are auto-created by getOrCreateFood when adding FoodLog entries
 */
@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    /**
     * Search foods by normalized food name
     * @param search the name of the food to search for, empty string returns all records
     * @param limit maximum number of items returned, default = 10
     * @return list of Food
     * Example: GET /api/foods?search=apple&limit=5
     */
    @GetMapping
    public ResponseEntity<List<Food>> searchFoods(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Food> foods = foodService.searchFoods(search);

        // limit the result array
        List<Food> result = foods.stream()
                .limit(limit)
                .toList();

        return ResponseEntity.ok(result);
    }
}