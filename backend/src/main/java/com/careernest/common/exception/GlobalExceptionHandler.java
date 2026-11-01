package com.careernest.common.exception;

import com.careernest.common.dto.ApiResponse;
import com.careernest.common.dto.FieldErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(ApiResponse.failure("Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
        List<FieldErrorResponse> errors = ex.getConstraintViolations().stream()
                .map(error -> new FieldErrorResponse(error.getPropertyPath().toString(), error.getMessage()))
                .toList();
        return ResponseEntity.badRequest().body(ApiResponse.failure("Validation failed", errors));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiResponse<Void>> handleMalformedRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ApiResponse.failure("Invalid request", null));
    }

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleRouteNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure("Resource not found", null));
    }

    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class})
    ResponseEntity<ApiResponse<Void>> handleUnauthorized(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    ResponseEntity<ApiResponse<Void>> handleForbidden(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    ResponseEntity<ApiResponse<Void>> handleServiceUnavailable(ServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled application error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Unexpected server error", null));
    }
}
