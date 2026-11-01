package com.careernest.modules.learning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LearningCourseRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be less than 200 characters")
        String title,

        String provider,
        String category,
        String status,
        Integer progress,
        String goal,
        String notes
) {}
