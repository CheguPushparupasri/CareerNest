package com.careernest.modules.dashboard.dto;

public record QuickActionResponse(
        String label,
        String description,
        String route,
        String icon
) {
}
