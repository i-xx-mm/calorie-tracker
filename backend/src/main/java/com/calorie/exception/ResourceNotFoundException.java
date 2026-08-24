package com.calorie.exception;
/**
 * Thrown when requested entity cannot be located inside database
 * Mapped to HTTP 404 Not Found by GlobalExceptionHandler
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}