package com.calorie.controller;

import com.calorie.dto.FoodRequest;
import com.calorie.model.Food;
import com.calorie.service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    /**
     * Search foods by name
     * @param search the name of the food to search for, required
     * @param limit max number of results to return, optional
     * @return list of Food
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