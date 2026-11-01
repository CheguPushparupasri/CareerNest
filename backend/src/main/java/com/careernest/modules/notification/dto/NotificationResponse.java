package com.careernest.modules.notification.dto;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        String type,
        String actionUrl,
        boolean isRead,
        Instant createdAt
) {}
