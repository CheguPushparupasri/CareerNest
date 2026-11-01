package com.careernest.modules.auth.dto;

import java.time.Instant;

public record SessionResponse(
        Long id,
        String deviceName,
        String ipAddress,
        String userAgent,
        Instant lastUsedAt,
        Instant expiresAt
) {
}
