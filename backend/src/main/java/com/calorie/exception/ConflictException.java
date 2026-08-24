package com.calorie.exception;

/**
 * Runtime exception thrown for conflicting resource scenarios
 * Typically used for duplicate username / duplicate record conflicts
 * Will be converted to HTTP 409 Conflict response by GlobalExceptionHandler
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}