package com.calorie.service;

import com.calorie.model.Food;
import com.calorie.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;

    public List<Food> searchFoods(String search) {
        String normalizedSearch = search.trim().toLowerCase();
        return foodRepository.searchByName(normalizedSearch);
    }

    /**
     * Get existing food or create if not exists
     * Uses unique constraint on (name, calorie) pair
     */
    public Food getOrCreateFood(String name, Integer calorie) {
        String normalizedName = name.trim().toLowerCase();
        return foodRepository.findByNameAndCalorie(normalizedName, calorie)
                .map(existingFood -> {
                    // refresh expiration timestamp when adding the food to food log
                    existingFood.setExpireAt(LocalDateTime.now().plusDays(30));
                    return foodRepository.save(existingFood);
                })
                .orElseGet(() -> {
                    Food newFood = createFood(normalizedName, calorie);
                    return newFood;
                });
    }

    private Food createFood(String name, Integer calorie) {
        Food food = new Food();
        food.setName(name);
        food.setCalorie(calorie);
        food.setExpireAt(LocalDateTime.now().plusDays(30));
        return foodRepository.save(food);
    }
}