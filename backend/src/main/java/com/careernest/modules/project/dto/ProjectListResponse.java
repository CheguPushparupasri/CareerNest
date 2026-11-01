package com.careernest.modules.project.dto;

import java.time.Instant;

public record ProjectListResponse(
        Long id,
        String title,
        String description,
        String githubUrl,
        String liveDemoUrl,
        String techStack,
        String status,
        boolean featured,
        Instant createdAt,
        int screenshotCount
) {}
