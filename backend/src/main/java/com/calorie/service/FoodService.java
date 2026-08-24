package com.calorie.service;

import com.calorie.model.Food;
import com.calorie.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Food entity
 * Handles food search and get/create logic with TTL expiration for MongoDB cleanup
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;

    /**
     * Performs case-insensitive search for Food by food name
     * Trims and converts input search term to lowercase before searching
     *
     * @param search search keyword
     * @return list of matching Food entities, empty list when no matches found
     */
    public List<Food> searchFoods(String search) {
        String normalizedSearch = search.trim().toLowerCase();
        return foodRepository.searchByName(normalizedSearch);
    }

    /**
     * Get existing food or create if not exists
     * Uses unique constraint on (name, calorie) pair
     * Refresh expireAt timestamp to 30-day future when existing record is hit
     *
     * @param name food name
     * @param calorie calorie of the food
     * @return persisted Food entity
     */
    public Food getOrCreateFood(String name, Integer calorie) {
        String normalizedName = name.trim().toLowerCase();
        return foodRepository.findByNameAndCalorie(normalizedName, calorie)
                .map(existingFood -> {
                    // refresh expiration timestamp when adding the food to food log
                    existingFood.setExpireAt(LocalDateTime.now().plusDays(30));
                    return foodRepository.save(existingFood);
                })
                .orElseGet(() -> createFood(normalizedName, calorie));
    }

    /**
     * Helper method: Creates and persists a new Food template record with 30-day expiration timestamp
     *
     * @param name food name
     * @param calorie calorie of the food
     * @return saved Food entity from database
     */
    private Food createFood(String name, Integer calorie) {
        Food food = new Food();
        food.setName(name);
        food.setCalorie(calorie);
        food.setExpireAt(LocalDateTime.now().plusDays(30));
        return foodRepository.save(food);
    }
}