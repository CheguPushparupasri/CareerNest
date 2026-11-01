package com.careernest.modules.health.dto;

import java.util.List;

public record CareerHealthResponse(
        int overallScore,
        int resumeScore,
        int skillsScore,
        int placementReadiness,
        int interviewReadiness,
        int learningProgress,
        int codingConsistency,
        int projectStrength,
        List<String> recommendations,
        List<String> strengths,
        List<String> weaknesses,
        List<String> roadmap
) {}
