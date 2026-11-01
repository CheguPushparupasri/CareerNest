package com.careernest.modules.dashboard.dto;

public record RecentCertificationResponse(
        String title,
        String issuer,
        String status,
        String issuedAt
) {
}
