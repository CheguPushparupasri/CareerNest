package com.careernest.modules.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be less than 200 characters")
        String title,
        
        String description,
        
        String githubUrl,
        
        String liveDemoUrl,
        
        @Size(max = 500, message = "Tech stack must be less than 500 characters")
        String techStack,
        
        String status,
        
        String startDate,
        
        String endDate,
        
        boolean featured
) {}
