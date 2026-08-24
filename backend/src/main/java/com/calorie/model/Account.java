package com.calorie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB document for user authentication Account
 * Stores login credential information, separate from User profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "account")
public class Account {
    /**
     * MongoDB document unique identifier
     */
    @Id
    private String id;

    /**
     * Unique login username for authentication
     * Enforced unique by MongoDB index constraint
     */
    @Indexed(unique = true)
    private String username;

    /**
     * Bcrypt-hashed user password
     */
    private String password;

    /**
     * Timestamp when this Account record was created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp of last Account record modification
     * Defaults to object instantiation time
     */
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}