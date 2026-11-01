package com.careernest.modules.resume.dto;

import java.time.Instant;

public record ResumeResponse(
        Long id,
        String title,
        String template,
        String content,
        boolean isPrimary,
        Integer atsScore,
        Instant createdAt,
        Instant updatedAt
) {}
