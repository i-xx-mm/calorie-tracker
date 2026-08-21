package com.calorie.repository;

import com.calorie.model.FoodLog;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodLogRepository extends MongoRepository<FoodLog, String> {

    @Query("{ 'username': ?0, 'date': { $gte: ?1, $lt: ?2 } }")
    Optional<FoodLog> findLogByUsernameAndEstDay(String username, LocalDateTime utcStart, LocalDateTime utcEnd);

    @Query("{ 'username': ?0, 'date': { $gte: ?1, $lt: ?2 } }")
    List<FoodLog> findLogsInEstRange(String username, LocalDateTime utcRangeStart, LocalDateTime utcRangeEnd);

    List<FoodLog> findByUsername(String username);
}