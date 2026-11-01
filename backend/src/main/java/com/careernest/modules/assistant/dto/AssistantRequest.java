package com.careernest.modules.assistant.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequest(
        @NotBlank String prompt,
        String type,
        String targetRole
) {}
