package com.careernest.modules.timeline.dto;

import jakarta.validation.constraints.NotBlank;

public record TimelineEntryRequest(
        @NotBlank String entryType,
        @NotBlank String title,
        String description,
        String organization,
        String entryDate
) {}
