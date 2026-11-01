package com.careernest.modules.dashboard.dto;

public record LearningProgressResponse(
        String track,
        int progress,
        String status
) {
}
