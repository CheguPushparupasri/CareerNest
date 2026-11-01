package com.careernest.modules.dashboard.dto;

import java.time.Instant;

public record ActivityFeedResponse(
        String type,
        String title,
        String description,
        Instant createdAt
) {
}
