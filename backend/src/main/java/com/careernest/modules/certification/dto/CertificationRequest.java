package com.careernest.modules.certification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CertificationRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be less than 200 characters")
        String title,

        @NotBlank(message = "Issuer is required")
        @Size(max = 200, message = "Issuer must be less than 200 characters")
        String issuer,

        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must be less than 100 characters")
        String category,

        String description,
        String credentialUrl,
        String imageUrl,
        String issueDate,
        String expiryDate,
        boolean verified
) {}
