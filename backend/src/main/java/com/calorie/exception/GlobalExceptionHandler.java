package com.calorie.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global REST API exception advice
 * Intercepts business runtime exceptions and input validation failures,
 * transforms all exceptions into consistent ErrorResponse JSON response with correct HTTP status codes
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle resource-not-found exception
     *
     * @param ex thrown resource not found exception
     * @param webRequest incoming web request context
     * @return standardized error JSON response with HTTP 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest webRequest) {
        ErrorResponse error = ErrorResponse.builder()
                .status("error")
                .code(404)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle authentication failure case
     *
     * @param ex thrown unauthorized exception
     * @param webRequest incoming web request context
     * @return standardized error JSON response with HTTP 401 UNAUTHORIZED
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, WebRequest webRequest) {
        ErrorResponse error = ErrorResponse.builder()
                .status("error")
                .code(401)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle permission denied scenario
     *
     * @param ex forbidden access exception
     * @param request incoming web request context
     * @return standardized error JSON response with HTTP 403 FORBIDDEN
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status("error")
                .code(403)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle resource conflict such as duplicate username
     * @param ex conflict exception
     * @param request incoming web request context
     * @return standardized error JSON response with HTTP 409 CONFLICT
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status("error")
                .code(409)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handle DTO input validation failure from @Valid annotation
     * Collects per-field validation error messages inside errors map
     *
     * @param ex method argument validation exception
     * @return validation error response with field error details and HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse error = ErrorResponse.builder()
                .status("error")
                .code(400)
                .message("Validation failed")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback catch-all handler for uncaught runtime exception
     * Full stack trace will be logged on server side
     * Raw exception details are NOT exposed to API client
     *
     * @param ex any unhandled exception
     * @return generic internal‑error JSON response and HTTP 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Global error caught: ", ex);
        ErrorResponse error = ErrorResponse.builder()
                .status("error")
                .code(500)
                .message("Internal server error")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}