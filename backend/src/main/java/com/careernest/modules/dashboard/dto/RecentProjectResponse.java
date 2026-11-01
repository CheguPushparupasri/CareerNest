package com.careernest.modules.dashboard.dto;

public record RecentProjectResponse(
        String title,
        String status,
        String technologySummary,
        String updatedAt
) {
}
