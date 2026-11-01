package com.careernest.modules.project.dto;

public record ScreenshotResponse(
        Long id,
        String imageUrl,
        Integer displayOrder
) {}
