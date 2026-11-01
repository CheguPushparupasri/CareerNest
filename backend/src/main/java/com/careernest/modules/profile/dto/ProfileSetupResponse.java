package com.careernest.modules.profile.dto;

public record ProfileSetupResponse(
        String fullName,
        String headline,
        String phone,
        String location,
        String educationSummary,
        String portfolioUrl,
        String linkedinUrl,
        String githubUrl,
        String profilePhotoUrl,
        boolean completed
) {
}
