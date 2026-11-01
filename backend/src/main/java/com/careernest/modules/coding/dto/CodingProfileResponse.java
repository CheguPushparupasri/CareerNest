package com.careernest.modules.coding.dto;

import java.time.Instant;

public record CodingProfileResponse(
        Long id,
        String platform,
        String username,
        String profileUrl,
        Integer rating,
        Integer problemsSolved,
        Integer streak,
        Integer badgeCount,
        Instant createdAt,
        Instant updatedAt
) {}
