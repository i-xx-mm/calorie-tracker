package com.calorie.exception;
/**
 * Thrown when user fails authentication: invalid token or missing credentials
 * Mapped to HTTP 401 Unauthorized via GlobalExceptionHandler
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}