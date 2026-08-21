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

    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable String id) {
        Food food = foodService.getFoodById(id);
        return ResponseEntity.ok(food);
    }

    /**
     * Create new food entry
     * Request body: { "name": "chicken", "calorie": 165, "note": "optional" }
     */
    @PostMapping
    public ResponseEntity<Food> createFood(
            @Valid @RequestBody FoodRequest request) {
        Food food = foodService.createFood(request.getName(), request.getCalorie());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(food);
    }

    /**
     * Update food entry
     * Request body: { "name": "chicken", "calorie": 165, "note": "optional" }
     */
    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(
            @PathVariable String id,
            @Valid @RequestBody FoodRequest request) {
        Food food = foodService.updateFood(id, request.getName(), request.getCalorie());
        return ResponseEntity.ok(food);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable String id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}