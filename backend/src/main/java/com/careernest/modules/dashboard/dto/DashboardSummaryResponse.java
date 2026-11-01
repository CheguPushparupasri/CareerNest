package com.careernest.modules.dashboard.dto;

import java.util.List;

public record DashboardSummaryResponse(
        DashboardUserResponse user,
        List<DashboardMetricResponse> statistics,
        CareerHealthResponse careerHealth,
        List<LearningProgressResponse> learningProgress,
        List<ChartPointResponse> applicationStatus,
        List<ChartPointResponse> activityTrend,
        List<RecentProjectResponse> recentProjects,
        List<RecentCertificationResponse> recentCertifications,
        List<UpcomingInterviewResponse> upcomingInterviews,
        List<NotificationResponse> notifications,
        List<ActivityFeedResponse> activityFeed,
        List<QuickActionResponse> quickActions,
        List<DashboardTaskResponse> upcomingTasks
) {
}
