package com.careernest.modules.dashboard.dto;

import java.time.Instant;

public record DashboardTaskResponse(
        String title,
        String type,
        String organization,
        Instant dueAt,
        String route
) {
}