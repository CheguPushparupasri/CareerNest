package com.careernest.modules.internship.dto;

import java.time.Instant;

public record InternshipApplicationResponse(
        Long id,
        String companyName,
        String position,
        String roleType,
        String location,
        String status,
        String source,
        String notes,
        Instant applicationDate,
        Instant deadline,
        String interviewStage,
        Instant createdAt,
        Instant updatedAt
) {}
