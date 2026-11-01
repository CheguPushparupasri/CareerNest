package com.careernest.modules.assistant.dto;

import java.util.List;

public record AssistantResponse(
        String category,
        String summary,
        String guidance,
        String nextSteps,
        List<String> actionItems
) {}
