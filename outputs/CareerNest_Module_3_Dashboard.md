# CareerNest Module 3: Complete Dashboard

## 1. Purpose of the Module

The Dashboard is the main authenticated landing experience for CareerNest users. It gives students and freshers a clear, actionable view of their career journey: profile readiness, applications, learning progress, upcoming interviews, certifications, projects, career timeline, notifications, and overall momentum.

The dashboard must feel like a real SaaS workspace: useful at a glance, fast to scan, responsive, accessible, theme-aware, and built from reusable UI components.

## 2. Functional Requirements

### Statistics

Show high-level career metrics:

- Profile completion percentage.
- Total resumes created.
- Active job applications.
- Upcoming interviews.
- Skills added.
- Completed roadmap tasks.
- Certifications earned.
- Projects added.

Each statistic card should support:

- Title.
- Value.
- Optional comparison text.
- Icon.
- Status color.
- Loading state.
- Empty state.

### Charts

Use Recharts for dashboard visualizations:

- Application status distribution.
- Applications over time.
- Learning progress by category.
- Skill proficiency breakdown.
- Interview practice activity.

Charts must handle:

- No data.
- Loading state.
- Mobile-friendly sizing.
- Dark mode colors.
- Tooltips.

### Career Timeline

Show chronological career milestones:

- Profile created.
- Resume generated.
- Project added.
- Certification added.
- Job application submitted.
- Interview scheduled.
- Roadmap task completed.

Timeline items should include:

- Event type.
- Title.
- Description.
- Date.
- Optional linked entity.
- Icon.

### Upcoming Interviews

Show scheduled interviews and preparation items:

- Company.
- Role.
- Interview round.
- Interview date/time.
- Mode: online, phone, onsite.
- Status.
- Preparation CTA.

### Recent Certifications

Show latest certifications:

- Certification title.
- Issuer.
- Issue date.
- Credential link.
- Thumbnail or icon.

### Recent Projects

Show latest projects:

- Project title.
- Tech stack.
- Short description.
- GitHub link.
- Live link.
- Last updated date.

### Learning Progress

Show roadmap and skill learning progress:

- Active roadmap target role.
- Completed roadmap tasks.
- Pending roadmap tasks.
- Skill categories.
- Weekly progress.

### Application Status

Show application pipeline:

- Saved.
- Applied.
- Screening.
- Interview.
- Offer.
- Rejected.
- Withdrawn.

Users should be able to click status cards/charts to navigate to filtered applications in the future implementation.

### Notifications

Show recent notifications:

- Interview reminders.
- Application follow-up reminders.
- Roadmap due dates.
- Profile completion suggestions.
- System messages.

Notifications should support:

- Read/unread state.
- Priority.
- Timestamp.
- Link target.

### Dark Mode

Support light and dark themes:

- Theme toggle in topbar.
- Persist user preference.
- Respect system preference on first visit.
- Use Tailwind `dark` strategy.
- Charts and cards must be readable in both modes.

### Responsive Layout

Support desktop, tablet, and mobile:

- Desktop: sidebar + multi-column dashboard.
- Tablet: collapsible navigation + two-column sections.
- Mobile: stacked sections + drawer/bottom navigation.

## 3. Non-Functional Requirements

- Dashboard API must be optimized to avoid excessive frontend requests.
- Frontend must render loading skeletons for async data.
- Dashboard widgets must be modular and reusable.
- Data access must be scoped to the authenticated user.
- Admin users may receive a different dashboard variant in future.
- UI must meet accessible contrast expectations in light and dark modes.
- Charts must not break layout on small screens.
- Dashboard should be extensible without becoming one large component.
- Backend aggregation queries must avoid N+1 issues.
- All endpoints must be protected by JWT authentication.

## 4. Database Tables

The dashboard mostly reads from existing module tables. It does not require many new core tables, but it depends on these entities:

```text
users
user_profiles
resumes
skills
user_skills
career_roadmaps
roadmap_items
jobs
job_applications
interview_sessions
certifications
projects
notifications
```

### Optional New Table: `career_timeline_events`

Recommended for scalable timeline rendering instead of building every event dynamically from many tables.

```text
id BIGINT PK
user_id BIGINT FK users(id)
event_type VARCHAR(80) NOT NULL
title VARCHAR(160) NOT NULL
description VARCHAR(500)
entity_type VARCHAR(80)
entity_id BIGINT
event_date DATETIME NOT NULL
created_at DATETIME
```

Recommended event types:

```text
PROFILE_CREATED
PROFILE_UPDATED
RESUME_CREATED
PROJECT_ADDED
CERTIFICATION_ADDED
APPLICATION_CREATED
APPLICATION_STATUS_CHANGED
INTERVIEW_SCHEDULED
ROADMAP_TASK_COMPLETED
SKILL_ADDED
```

Indexes:

```text
idx_timeline_user_date
idx_timeline_user_type
```

### Optional Enhancement: `dashboard_preferences`

Useful later for customizable widgets.

```text
id BIGINT PK
user_id BIGINT UNIQUE FK users(id)
theme VARCHAR(20)
widget_order JSON
hidden_widgets JSON
created_at DATETIME
updated_at DATETIME
```

For the first implementation, theme preference can also be stored client-side.

## 5. API Endpoints

Base path:

```text
/api/v1/dashboard
```

### Primary Dashboard Endpoint

```text
GET /api/v1/dashboard/summary
```

Returns all above-the-fold dashboard data:

- User greeting.
- Statistics.
- Application status summary.
- Learning progress summary.
- Upcoming interviews.
- Recent notifications.

### Widget-Specific Endpoints

```text
GET /api/v1/dashboard/statistics
GET /api/v1/dashboard/application-status
GET /api/v1/dashboard/application-trend
GET /api/v1/dashboard/learning-progress
GET /api/v1/dashboard/skill-breakdown
GET /api/v1/dashboard/career-timeline
GET /api/v1/dashboard/upcoming-interviews
GET /api/v1/dashboard/recent-certifications
GET /api/v1/dashboard/recent-projects
GET /api/v1/dashboard/notifications
```

### Notification Actions

```text
PATCH /api/v1/notifications/{notificationId}/read
PATCH /api/v1/notifications/read-all
```

### Recommended Query Parameters

```text
GET /api/v1/dashboard/career-timeline?limit=10
GET /api/v1/dashboard/application-trend?period=MONTHLY
GET /api/v1/dashboard/notifications?limit=5&unreadOnly=false
```

## 6. API Response Design

### Dashboard Summary Response

```json
{
  "success": true,
  "message": "Dashboard summary fetched successfully",
  "data": {
    "user": {
      "fullName": "Aarav Sharma",
      "role": "STUDENT",
      "profileSetupCompleted": true
    },
    "statistics": {
      "profileCompletion": 82,
      "totalResumes": 3,
      "activeApplications": 12,
      "upcomingInterviews": 2,
      "skillsAdded": 18,
      "completedRoadmapTasks": 24,
      "certifications": 5,
      "projects": 4
    },
    "applicationStatus": [
      { "status": "APPLIED", "count": 5 },
      { "status": "SCREENING", "count": 3 },
      { "status": "INTERVIEW", "count": 2 },
      { "status": "OFFER", "count": 1 },
      { "status": "REJECTED", "count": 4 }
    ],
    "learningProgress": {
      "targetRole": "Full Stack Developer",
      "completedTasks": 24,
      "totalTasks": 40,
      "percentage": 60
    },
    "upcomingInterviews": [],
    "notifications": []
  },
  "errors": null,
  "timestamp": "2026-07-17T00:00:00Z"
}
```

## 7. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/dashboard/
    controller/
      DashboardController.java
    dto/
      DashboardSummaryResponse.java
      DashboardStatisticsResponse.java
      ApplicationStatusChartResponse.java
      ApplicationTrendResponse.java
      LearningProgressResponse.java
      SkillBreakdownResponse.java
      CareerTimelineResponse.java
      UpcomingInterviewResponse.java
      RecentCertificationResponse.java
      RecentProjectResponse.java
      DashboardNotificationResponse.java
    entity/
      CareerTimelineEvent.java
      DashboardPreference.java
    repository/
      CareerTimelineRepository.java
      DashboardPreferenceRepository.java
      DashboardQueryRepository.java
    service/
      DashboardService.java
      DashboardServiceImpl.java
      CareerTimelineService.java
```

### Backend Responsibilities

- Aggregate dashboard data for the authenticated user.
- Calculate profile completion percentage.
- Count applications by status.
- Calculate roadmap progress.
- Fetch upcoming interviews.
- Fetch recent certifications/projects.
- Fetch recent notifications.
- Return chart-friendly DTOs.

## 8. Frontend Folder Structure

```text
frontend/
  src/
    features/
      dashboard/
        pages/
          DashboardPage.jsx
        components/
          DashboardHeader.jsx
          StatCard.jsx
          StatsGrid.jsx
          ApplicationStatusChart.jsx
          ApplicationTrendChart.jsx
          LearningProgressCard.jsx
          SkillBreakdownChart.jsx
          CareerTimeline.jsx
          UpcomingInterviewsCard.jsx
          RecentCertificationsCard.jsx
          RecentProjectsCard.jsx
          NotificationsPanel.jsx
          DashboardSkeleton.jsx
          DashboardEmptyState.jsx
        services/
          dashboardService.js
        hooks/
          useDashboardSummary.js
          useDashboardCharts.js
        utils/
          dashboardFormatters.js
    components/
      ui/
        Card.jsx
        StatCard.jsx
        ChartContainer.jsx
        ProgressBar.jsx
        Timeline.jsx
        NotificationItem.jsx
        EmptyState.jsx
        Skeleton.jsx
        ThemeToggle.jsx
    hooks/
      useTheme.js
    config/
      dashboardConfig.js
```

## 9. UI Design

### Visual Direction

CareerNest dashboard should feel calm, professional, and productivity-focused. It should not look like a marketing landing page. The design should support repeated daily use.

### Layout

Desktop layout:

```text
Topbar
Sidebar | Dashboard Content
```

Content sections:

```text
Dashboard Header
Statistics Grid
Main Chart Row
Career Timeline + Upcoming Interviews
Recent Projects + Recent Certifications
Learning Progress + Notifications
```

### Reusable Cards

All dashboard widgets should use reusable card primitives:

- `Card`
- `CardHeader`
- `CardTitle`
- `CardDescription`
- `CardContent`
- `CardFooter`

Dashboard cards should support:

- Light/dark mode.
- Loading skeletons.
- Empty state.
- Optional action button.
- Responsive height.

### Professional UI Details

- Use concise headings.
- Keep charts readable and not overly colorful.
- Use consistent spacing.
- Use icons for visual scanning.
- Avoid nested cards.
- Avoid decorative gradients or visual noise.
- Prioritize content density without feeling cramped.

## 10. Design System Additions

### Dashboard Colors

Use the Module 1 palette with chart-specific colors:

```text
Chart Blue:    #2563EB
Chart Teal:    #14B8A6
Chart Green:   #16A34A
Chart Amber:   #F59E0B
Chart Red:     #DC2626
Chart Violet:  #7C3AED
```

Dark mode:

```text
Background:    #020617
Surface:       #0F172A
Surface Muted: #1E293B
Border:        #334155
Text Strong:   #F8FAFC
Text Default:  #CBD5E1
Text Muted:    #94A3B8
```

### Typography

- Page title: 28px / 36px / 700.
- Section title: 18px / 28px / 600.
- Stat value: 28px / 36px / 700.
- Card body: 14px / 20px / 400.
- Metadata: 12px / 16px / 500.

### Spacing

- Page padding desktop: 24px to 32px.
- Page padding mobile: 16px.
- Grid gap: 16px to 24px.
- Card padding: 16px to 20px.

## 11. Security Considerations

- All dashboard endpoints require authentication.
- Users can only access their own dashboard data.
- Backend must derive `userId` from JWT principal, never from request body.
- Notification actions must validate ownership.
- Timeline events must validate ownership.
- Admin dashboard data must be separated from student dashboard data.
- Avoid exposing private notes or sensitive application details on summary endpoints unless needed.
- Apply pagination/limits to timeline and notification endpoints.

## 12. Future Scalability

The dashboard should be ready for:

- User-customizable widgets.
- Admin dashboard variant.
- Organization/team dashboard variant.
- AI career recommendations.
- Cached dashboard summary.
- Real-time notifications through WebSocket or SSE.
- Redis caching for expensive aggregation.
- Event-driven timeline generation.
- Feature flags for premium dashboard widgets.

## 13. Industry Best Practices

- Use one summary endpoint for initial dashboard load.
- Use widget-specific endpoints for refreshable or lazy-loaded sections.
- Keep charts presentation-friendly; transform data in backend DTOs where possible.
- Avoid fetching all raw records just to count on frontend.
- Use memoized frontend chart components.
- Use skeleton states, not layout-shifting spinners.
- Keep cards reusable and content-driven.
- Use accessible labels for charts and icons.
- Keep dark mode token-driven.
- Add backend tests for aggregation correctness.

## 14. Testing Strategy

### Backend Tests

- Dashboard summary requires authentication.
- User cannot access another user's data.
- Statistics are calculated correctly.
- Application status counts are correct.
- Learning progress percentage handles zero tasks.
- Upcoming interviews are sorted by soonest date.
- Recent projects and certifications are limited correctly.
- Notification unread counts are correct.

### Frontend Tests

- Dashboard renders loading skeleton.
- Dashboard renders empty states.
- Statistics cards render correct values.
- Charts handle empty data.
- Dark mode toggle persists preference.
- Responsive layout does not hide critical content.
- Notification read action updates UI.

### Visual QA

- Desktop: 1440px.
- Laptop: 1280px.
- Tablet: 768px.
- Mobile: 390px.
- Light mode and dark mode.

## 15. Swagger Documentation

Swagger group:

```text
Dashboard
```

Document:

- `GET /api/v1/dashboard/summary`
- `GET /api/v1/dashboard/statistics`
- `GET /api/v1/dashboard/application-status`
- `GET /api/v1/dashboard/application-trend`
- `GET /api/v1/dashboard/learning-progress`
- `GET /api/v1/dashboard/skill-breakdown`
- `GET /api/v1/dashboard/career-timeline`
- `GET /api/v1/dashboard/upcoming-interviews`
- `GET /api/v1/dashboard/recent-certifications`
- `GET /api/v1/dashboard/recent-projects`
- `GET /api/v1/dashboard/notifications`

Each endpoint must document:

- Authentication requirement.
- Response schema.
- Empty-state examples.
- Error response.
- Query parameters.

## 16. Implementation Order After Approval

1. Define backend dashboard DTOs.
2. Create optional timeline/preference entities.
3. Create dashboard repositories and aggregation queries.
4. Implement dashboard service.
5. Implement dashboard controller.
6. Add Swagger annotations.
7. Add backend tests.
8. Create frontend reusable dashboard cards.
9. Create dashboard service and hooks.
10. Build statistics grid.
11. Build Recharts widgets.
12. Build timeline, interviews, projects, certifications, progress, and notifications widgets.
13. Add dark mode tokens and theme toggle.
14. Validate responsive layout.
15. Add frontend tests and visual QA.

## 17. Approval Gate

No dashboard implementation should begin until this dashboard architecture is approved.

After approval, implementation should begin with the backend dashboard summary API and reusable frontend card system.
