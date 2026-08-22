package com.calorie.repository;

import com.calorie.model.Food;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends MongoRepository<Food, String> {
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Food> searchByName(String name);

    Optional<Food> findByNameAndCalorie(String name, Integer calorie);
}