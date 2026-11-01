package com.careernest.modules.dashboard.dto;

public record DashboardMetricResponse(
        String key,
        String label,
        long value,
        String suffix,
        String trend,
        String tone
) {
}
