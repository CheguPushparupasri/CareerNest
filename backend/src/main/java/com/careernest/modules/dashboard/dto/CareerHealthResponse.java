package com.careernest.modules.dashboard.dto;

import java.util.List;

public record CareerHealthResponse(
        int overallScore,
        int resumeScore,
        int skillScore,
        int placementReadiness,
        int learningProgress,
        int codingConsistency,
        int projectStrength,
        List<String> recommendations
) {
}
