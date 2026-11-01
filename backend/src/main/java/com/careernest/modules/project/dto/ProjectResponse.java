package com.careernest.modules.project.dto;

import java.time.Instant;
import java.util.List;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        String githubUrl,
        String liveDemoUrl,
        String techStack,
        String status,
        Instant startDate,
        Instant endDate,
        boolean featured,
        Instant createdAt,
        Instant updatedAt,
        List<ScreenshotResponse> screenshots
) {}
