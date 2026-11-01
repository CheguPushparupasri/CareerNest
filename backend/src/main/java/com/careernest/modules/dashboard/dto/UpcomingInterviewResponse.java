package com.careernest.modules.dashboard.dto;

public record UpcomingInterviewResponse(
        String company,
        String role,
        String round,
        String scheduledAt,
        String mode
) {
}
