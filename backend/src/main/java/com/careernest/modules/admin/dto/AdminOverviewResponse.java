package com.careernest.modules.admin.dto;

public record AdminOverviewResponse(
        long totalUsers,
        long activeUsers,
        long totalProjects,
        long totalResumes,
        long totalCertifications,
        long totalApplications,
        long totalLearningCourses,
        long totalCodingProfiles
) {}
