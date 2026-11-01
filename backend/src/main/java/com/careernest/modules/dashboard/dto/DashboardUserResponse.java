package com.careernest.modules.dashboard.dto;

public record DashboardUserResponse(
        String fullName,
        String role,
        boolean emailVerified,
        boolean profileSetupCompleted
) {
}
