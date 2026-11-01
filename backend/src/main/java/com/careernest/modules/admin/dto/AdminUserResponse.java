package com.careernest.modules.admin.dto;

import java.time.Instant;

public record AdminUserResponse(
        Long id,
        String publicId,
        String fullName,
        String email,
        String role,
        String accountStatus,
        boolean emailVerified,
        boolean profileSetupCompleted,
        Instant lastLoginAt,
        Instant createdAt
) {}
