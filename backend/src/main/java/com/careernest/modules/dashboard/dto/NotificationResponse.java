package com.careernest.modules.dashboard.dto;

public record NotificationResponse(
        String title,
        String message,
        String priority,
        boolean read,
        String createdAt
) {
}
