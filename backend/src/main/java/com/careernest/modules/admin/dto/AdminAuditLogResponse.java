package com.careernest.modules.admin.dto;

import java.time.Instant;

public record AdminAuditLogResponse(
        Long id,
        String userEmail,
        String actorEmail,
        String eventType,
        String eventDescription,
        String ipAddress,
        Instant createdAt
) {}
