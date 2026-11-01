package com.careernest.modules.profile.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileSetupRequest(
        @Size(max = 120) String fullName,
        @Size(max = 160) String headline,
        @Size(max = 30) String phone,
        @Size(max = 120) String location,
        @Size(max = 255) String educationSummary,
        @Pattern(regexp = "^(https?://.*)?$", message = "Portfolio URL must be a valid URL") String portfolioUrl,
        @Pattern(regexp = "^(https?://.*)?$", message = "LinkedIn URL must be a valid URL") String linkedinUrl,
        @Pattern(regexp = "^(https?://.*)?$", message = "GitHub URL must be a valid URL") String githubUrl
) {
}
