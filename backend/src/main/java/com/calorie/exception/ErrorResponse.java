package com.calorie.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized JSON error response payload returned to API client.
 * Unified error body produced by GlobalExceptionHandler for all exceptions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    /**
     * Fixed status marker: "error" for failure responses.
     */
    private String status;

    /**
     * Numeric HTTP status code e.g. 400,404,409,500.
     */
    private Integer code;

    /**
     * Human-readable top‑level error description
     */
    private String message;

    /**
     * Field-level validation error map
     * key = field name, value = validation message
     * Null when not validation error
     */
    private Map<String, String> errors;

    /**
     * UTC timestamp when this error response was generated
     */
    private LocalDateTime timestamp;
}