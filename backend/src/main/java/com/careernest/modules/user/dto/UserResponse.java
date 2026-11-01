package com.careernest.modules.user.dto;

public record UserResponse(
        String id,
        String fullName,
        String email,
        String role,
        boolean emailVerified,
        boolean profileSetupCompleted,
        String accountStatus
) {
}
