package com.calorie.repository;

import com.calorie.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for User document
 * Handles user profile data access operations
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Find user profile document by username
     * @param username target username
     * @return Optional User profile, empty if not present
     */
    Optional<User> findByUsername(String username);
}