package com.careernest.modules.resume.dto;

import java.time.Instant;

public record ResumeVersionResponse(
        Long id,
        Integer versionNumber,
        String content,
        String changeDescription,
        Instant createdAt
) {}
