package com.calorie.repository;

import com.calorie.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for Account document
 * Provides data access operations for user account persistence
 */
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    /**
     * Lookup account document by exact username match
     *
     * @param username target username
     * @return Optional containing matched Account, empty if no match found
     */
    Optional<Account> findByUsername(String username);

    /**
     * Check whether an account already exists with given username
     * Used for registration duplicate-username validation
     *
     * @param username target username
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
}