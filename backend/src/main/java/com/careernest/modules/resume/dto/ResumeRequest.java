package com.careernest.modules.resume.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResumeRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be less than 200 characters")
        String title,
        
        @NotBlank(message = "Template is required")
        String template,
        
        String content,
        
        boolean isPrimary
) {}
