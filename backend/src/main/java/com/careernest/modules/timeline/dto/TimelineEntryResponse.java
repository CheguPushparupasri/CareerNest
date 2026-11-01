package com.careernest.modules.timeline.dto;

import java.time.Instant;

public record TimelineEntryResponse(
        Long id,
        String entryType,
        String title,
        String description,
        String organization,
        Instant entryDate,
        Instant createdAt,
        Instant updatedAt
) {}
