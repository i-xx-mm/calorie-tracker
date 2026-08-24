package com.calorie.repository;

import com.calorie.model.Food;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for Food document
 * Manages food item templates used inside user food logs
 */
@Repository
public interface FoodRepository extends MongoRepository<Food, String> {
    /**
     * Case-insensitive regex search over food name for UI autocomplete
     *
     * @param name partial or full food name search term
     * @return list of matched Food documents
     */
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Food> searchByName(String name);

    /**
     * Lookup existing food by normalized name and calorie value
     *
     * @param name normalized lowercase food name
     * @param calorie calorie value of food
     * @return Optional matching Food, empty if no match
     */
    Optional<Food> findByNameAndCalorie(String name, Integer calorie);
}