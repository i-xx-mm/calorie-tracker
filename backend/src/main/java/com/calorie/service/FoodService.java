package com.calorie.service;

import com.calorie.exception.ConflictException;
import com.calorie.exception.ResourceNotFoundException;
import com.calorie.model.Food;
import com.calorie.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public Food getFoodById(String id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));
    }

    public Food createFood(String name, Integer calorie) {
        String normalizedName = name.trim().toLowerCase();

        if (foodRepository.existsByNameAndCalorie(normalizedName, calorie)) {
            throw new ConflictException("Food entry with this name and calorie value already exists");
        }

        Food food = Food.builder()
                .name(normalizedName)
                .calorie(calorie)
                .build();
        return foodRepository.save(food);
    }

    public Food updateFood(String id, String name, Integer calorie) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        String normalizedName = name.trim().toLowerCase();

        if (!food.getName().equals(normalizedName) || !food.getCalorie().equals(calorie)) {
            if (foodRepository.existsByNameAndCalorie(normalizedName, calorie)) {
                throw new ConflictException("Food entry with this name and calorie value already exists");
            }
        }

        food.setName(normalizedName);
        food.setCalorie(calorie);
        return foodRepository.save(food);
    }

    public void deleteFood(String id) {
        if (!foodRepository.existsById(id)) {
            throw new ResourceNotFoundException("Food not found");
        }
        foodRepository.deleteById(id);
    }

    /**
     * Get existing food or create if not exists
     * Uses unique constraint on (name, calorie) pair
     */
    public Food getOrCreateFood(String name, Integer calorie) {
        String normalizedName = name.trim().toLowerCase();
        return foodRepository.findByNameAndCalorie(normalizedName, calorie)
                .orElseGet(() -> createFood(normalizedName, calorie));
    }
}