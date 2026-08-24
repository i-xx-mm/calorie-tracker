package com.calorie.repository;

import com.calorie.model.FoodLog;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for FoodLog daily calorie log document
 * Supports time-range queries for user food intake records
 */
@Repository
public interface FoodLogRepository extends MongoRepository<FoodLog, String> {

    /**
     * Find single daily FoodLog for specified user within given UTC time range
     *
     * @param username target username
     * @param utcStart inclusive UTC start timestamp of day range
     * @param utcEnd exclusive UTC end timestamp of day range
     * @return Optional with matching FoodLog, empty when no log exists for that day
     */
    @Query("{ 'username': ?0, 'date': { $gte: ?1, $lt: ?2 } }")
    Optional<FoodLog> findLogByUsernameAndEstDay(String username, LocalDateTime utcStart, LocalDateTime utcEnd);

    /**
     * Fetch multiple FoodLog entries for user inside provided UTC time range
     * Used for historical calorie report and date range browsing
     *
     * @param username target username
     * @param utcRangeStart inclusive start of UTC query window
     * @param utcRangeEnd exclusive end of UTC query window
     * @return list of matching FoodLog documents, empty list if none found
     */
    @Query("{ 'username': ?0, 'date': { $gte: ?1, $lt: ?2 } }")
    List<FoodLog> findLogsInEstRange(String username, LocalDateTime utcRangeStart, LocalDateTime utcRangeEnd);
}