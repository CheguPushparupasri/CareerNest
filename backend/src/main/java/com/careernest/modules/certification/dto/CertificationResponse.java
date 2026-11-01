package com.careernest.modules.certification.dto;

import java.time.Instant;

public record CertificationResponse(
        Long id,
        String title,
        String issuer,
        String category,
        String description,
        String credentialUrl,
        String imageUrl,
        Instant issueDate,
        Instant expiryDate,
        boolean verified,
        Instant createdAt,
        Instant updatedAt
) {}
