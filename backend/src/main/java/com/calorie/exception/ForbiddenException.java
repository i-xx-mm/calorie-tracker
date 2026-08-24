package com.calorie.exception;

/**
 * Thrown when authenticated user has no permission to access target resource
 * Mapped to HTTP 403 Forbidden status via GlobalExceptionHandler
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}