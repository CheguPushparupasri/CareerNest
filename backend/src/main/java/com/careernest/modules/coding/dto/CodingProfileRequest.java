package com.careernest.modules.coding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CodingProfileRequest(
        @NotBlank(message = "Platform is required")
        @Size(max = 100, message = "Platform must be less than 100 characters")
        String platform,

        @NotBlank(message = "Username is required")
        @Size(max = 200, message = "Username must be less than 200 characters")
        String username,

        String profileUrl,
        Integer rating,
        Integer problemsSolved,
        Integer streak,
        Integer badgeCount
) {}
