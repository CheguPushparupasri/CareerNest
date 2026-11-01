package com.careernest.modules.learning.dto;

import java.time.Instant;

public record LearningCourseResponse(
        Long id,
        String title,
        String provider,
        String category,
        String status,
        int progress,
        String goal,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
