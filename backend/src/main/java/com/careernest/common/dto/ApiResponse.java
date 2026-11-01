package com.careernest.common.dto;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Object errors,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, Instant.now());
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, null, Instant.now());
    }

    public static ApiResponse<Void> failure(String message, Object errors) {
        return new ApiResponse<>(false, message, null, errors, Instant.now());
    }
}
