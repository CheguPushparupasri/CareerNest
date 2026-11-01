package com.careernest.modules.internship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InternshipApplicationRequest(
        @NotBlank(message = "Company name is required")
        @Size(max = 200, message = "Company name must be less than 200 characters")
        String companyName,

        @NotBlank(message = "Position is required")
        @Size(max = 200, message = "Position must be less than 200 characters")
        String position,

        String roleType,
        String location,
        String status,
        String source,
        String notes,
        String applicationDate,
        String deadline,
        String interviewStage
) {}
