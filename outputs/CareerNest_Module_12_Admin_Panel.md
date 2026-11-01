# CareerNest Module 12: Admin Panel

## 1. Purpose of the Module

The Admin Panel is the internal operations and governance console for CareerNest. It allows authorized administrators to manage users, view analytics, generate reports, review activity logs, send notifications, handle approvals, manage platform content, monitor system health, and enforce security controls.

This module must be built as a secure enterprise admin system with strong role-based access, audit logging, least-privilege permissions, and operational visibility.

## 2. Functional Requirements

### Manage Users

Admins can:

- View users.
- Search users by name, email, role, status.
- Filter users by role, status, verification state, signup date, last login.
- View user profile summary.
- Enable or disable users.
- Lock or unlock users.
- Change user role, restricted to `SUPER_ADMIN`.
- Force logout user sessions.
- View user activity summary.

Sensitive actions must require audit logging.

### Analytics

Admin analytics should show:

- Total users.
- New users over time.
- Active users.
- Verified users.
- Profile completion rate.
- Resume creation count.
- Project count.
- Certification count.
- Internship application activity.
- Learning tracker activity.
- AI assistant usage.
- Most used modules.

Analytics should support date ranges:

- Today.
- Last 7 days.
- Last 30 days.
- This month.
- Custom range.

### Reports

Admins can generate reports:

- User growth report.
- Engagement report.
- Resume usage report.
- Internship application report.
- Certification report.
- Learning progress report.
- AI usage report.
- Security report.

Report formats:

- Dashboard table initially.
- CSV export.
- PDF export in future.

### Activity Logs

Admins can view activity logs:

- User login.
- Failed login.
- Password reset.
- Role changes.
- Account status changes.
- Certificate verification changes.
- Content updates.
- Admin actions.
- AI usage events.

Logs should be filterable by:

- User.
- Actor.
- Event type.
- Date range.
- IP address.
- Severity.

### Notifications

Admins can:

- Send platform announcements.
- Send targeted notifications by role or user segment.
- Schedule notifications.
- View delivery status.
- Mark announcements active/inactive.

Notification channels:

- In-app initially.
- Email in future.

### Approvals

Approval workflows can include:

- Certificate verification.
- Public portfolio approval, future.
- Content publication approval.
- Reported content review, future.
- Admin role assignment approval, future.

Approval states:

```text
PENDING
APPROVED
REJECTED
NEEDS_CHANGES
```

### Content Management

Admins can manage platform content:

- Learning resources.
- Interview questions.
- Skill taxonomy.
- Certification categories.
- Resume templates.
- Job/internship opportunities.
- Announcements.
- Badges.

Content should support draft/published/archived states.

### Monitoring

Admin monitoring should show:

- API health.
- Database connectivity.
- Cloudinary status, if configured.
- AI provider status.
- Error count.
- Average response time.
- Failed jobs.
- Recent exceptions.

Initial monitoring can use backend health endpoints and aggregated logs. Full observability can be added later.

### Admin Dashboard

Admin dashboard should include:

- User growth chart.
- Active users.
- Module usage.
- Pending approvals.
- Recent admin actions.
- Security alerts.
- System health summary.
- AI usage summary.

### Security

Security administration includes:

- Role management.
- User status management.
- Session revocation.
- Audit log review.
- Suspicious activity indicators.
- Failed login tracking.
- Admin action logging.

## 3. Non-Functional Requirements

- Admin APIs must require authentication and admin authorization.
- `SUPER_ADMIN` actions must be separate from normal admin actions.
- All sensitive admin actions must be audited.
- Admin list endpoints must support pagination.
- Admin analytics must be backend-aggregated.
- Admin UI must be dense, professional, and efficient.
- No sensitive data should be exposed unnecessarily.
- Admin actions must validate target user and actor permissions.
- Monitoring endpoints must not expose secrets.
- Reports should be generated safely without loading excessive data into memory.

## 4. Database Tables

This module reuses many existing tables:

```text
users
roles
user_sessions
audit_logs
notifications
certifications
projects
resumes
internship_applications
learning_resources
ai_requests
```

### `admin_notifications`

Stores admin-created announcements and targeted notifications.

```text
id BIGINT PK
public_id VARCHAR(64) UNIQUE NOT NULL
created_by BIGINT FK users(id)
title VARCHAR(180) NOT NULL
message TEXT NOT NULL
target_type VARCHAR(60) NOT NULL
target_role VARCHAR(60)
target_user_id BIGINT NULL FK users(id)
priority VARCHAR(40) NOT NULL
status VARCHAR(40) NOT NULL
scheduled_at DATETIME NULL
sent_at DATETIME NULL
created_at DATETIME
updated_at DATETIME
```

### `approval_requests`

Stores approval workflows.

```text
id BIGINT PK
public_id VARCHAR(64) UNIQUE NOT NULL
request_type VARCHAR(80) NOT NULL
requested_by BIGINT FK users(id)
reviewed_by BIGINT NULL FK users(id)
source_entity_type VARCHAR(80)
source_entity_id BIGINT
status VARCHAR(40) NOT NULL
review_note VARCHAR(500)
created_at DATETIME
reviewed_at DATETIME NULL
updated_at DATETIME
```

### `content_items`

Generic managed content table for simple platform content.

```text
id BIGINT PK
public_id VARCHAR(64) UNIQUE NOT NULL
content_type VARCHAR(80) NOT NULL
title VARCHAR(180) NOT NULL
body TEXT
metadata_json JSON
status VARCHAR(40) NOT NULL
created_by BIGINT FK users(id)
updated_by BIGINT NULL FK users(id)
published_at DATETIME NULL
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

### `system_health_snapshots`

Optional table for periodic monitoring snapshots.

```text
id BIGINT PK
service_name VARCHAR(120) NOT NULL
status VARCHAR(40) NOT NULL
response_time_ms INT
details_json JSON
captured_at DATETIME NOT NULL
```

## 5. API Endpoints

Base path:

```text
/api/v1/admin
```

### User Management

```text
GET   /api/v1/admin/users
GET   /api/v1/admin/users/{userId}
PATCH /api/v1/admin/users/{userId}/status
PATCH /api/v1/admin/users/{userId}/role
POST  /api/v1/admin/users/{userId}/force-logout
GET   /api/v1/admin/users/{userId}/activity
```

### Analytics

```text
GET /api/v1/admin/analytics/summary
GET /api/v1/admin/analytics/users
GET /api/v1/admin/analytics/modules
GET /api/v1/admin/analytics/ai-usage
GET /api/v1/admin/analytics/security
```

### Reports

```text
GET  /api/v1/admin/reports
POST /api/v1/admin/reports/generate
GET  /api/v1/admin/reports/{reportId}
GET  /api/v1/admin/reports/{reportId}/download
```

### Activity Logs

```text
GET /api/v1/admin/activity-logs
GET /api/v1/admin/activity-logs/{logId}
```

### Notifications

```text
POST   /api/v1/admin/notifications
GET    /api/v1/admin/notifications
GET    /api/v1/admin/notifications/{notificationId}
PATCH  /api/v1/admin/notifications/{notificationId}/status
DELETE /api/v1/admin/notifications/{notificationId}
```

### Approvals

```text
GET   /api/v1/admin/approvals
GET   /api/v1/admin/approvals/{approvalId}
PATCH /api/v1/admin/approvals/{approvalId}/approve
PATCH /api/v1/admin/approvals/{approvalId}/reject
PATCH /api/v1/admin/approvals/{approvalId}/needs-changes
```

### Content Management

```text
POST   /api/v1/admin/content
GET    /api/v1/admin/content
GET    /api/v1/admin/content/{contentId}
PUT    /api/v1/admin/content/{contentId}
PATCH  /api/v1/admin/content/{contentId}/publish
PATCH  /api/v1/admin/content/{contentId}/archive
DELETE /api/v1/admin/content/{contentId}
```

### Monitoring

```text
GET /api/v1/admin/monitoring/health
GET /api/v1/admin/monitoring/errors
GET /api/v1/admin/monitoring/jobs
GET /api/v1/admin/monitoring/integrations
```

## 6. API Contracts

### Update User Status Request

```json
{
  "status": "DISABLED",
  "reason": "Suspicious activity detected"
}
```

### Update User Role Request

```json
{
  "role": "ADMIN",
  "reason": "Promoted to content administrator"
}
```

### Admin Analytics Summary Response

```json
{
  "totalUsers": 1240,
  "activeUsers": 812,
  "newUsersThisMonth": 143,
  "verifiedUsers": 1060,
  "pendingApprovals": 18,
  "reportsGenerated": 24,
  "securityAlerts": 3,
  "moduleUsage": [
    { "module": "Resume Builder", "activeUsers": 420 },
    { "module": "Internship Tracker", "activeUsers": 310 }
  ]
}
```

### Create Admin Notification Request

```json
{
  "title": "New Resume Templates Available",
  "message": "We added new ATS-friendly resume templates for internship applications.",
  "targetType": "ROLE",
  "targetRole": "STUDENT",
  "priority": "NORMAL",
  "scheduledAt": null
}
```

### Approval Review Request

```json
{
  "reviewNote": "Certificate verified using the credential URL."
}
```

## 7. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/admin/
    controller/
      AdminUserController.java
      AdminAnalyticsController.java
      AdminReportController.java
      AdminActivityLogController.java
      AdminNotificationController.java
      AdminApprovalController.java
      AdminContentController.java
      AdminMonitoringController.java
    dto/
      AdminUserResponse.java
      AdminUserSearchCriteria.java
      UpdateUserStatusRequest.java
      UpdateUserRoleRequest.java
      AdminAnalyticsSummaryResponse.java
      AdminModuleUsageResponse.java
      AdminReportRequest.java
      AdminReportResponse.java
      AdminActivityLogResponse.java
      AdminNotificationRequest.java
      AdminNotificationResponse.java
      ApprovalRequestResponse.java
      ApprovalReviewRequest.java
      ContentItemRequest.java
      ContentItemResponse.java
      MonitoringHealthResponse.java
    entity/
      AdminNotification.java
      ApprovalRequest.java
      ContentItem.java
      SystemHealthSnapshot.java
      ApprovalStatus.java
      ContentStatus.java
    repository/
      AdminNotificationRepository.java
      ApprovalRequestRepository.java
      ContentItemRepository.java
      SystemHealthSnapshotRepository.java
      AdminAnalyticsQueryRepository.java
    service/
      AdminUserService.java
      AdminAnalyticsService.java
      AdminReportService.java
      AdminActivityLogService.java
      AdminNotificationService.java
      AdminApprovalService.java
      AdminContentService.java
      AdminMonitoringService.java
    mapper/
      AdminMapper.java
```

## 8. Frontend Folder Structure

```text
frontend/
  src/
    features/
      admin/
        pages/
          AdminDashboardPage.jsx
          AdminUsersPage.jsx
          AdminUserDetailsPage.jsx
          AdminAnalyticsPage.jsx
          AdminReportsPage.jsx
          AdminActivityLogsPage.jsx
          AdminNotificationsPage.jsx
          AdminApprovalsPage.jsx
          AdminContentPage.jsx
          AdminMonitoringPage.jsx
          AdminSecurityPage.jsx
        components/
          AdminStatsGrid.jsx
          AdminUserTable.jsx
          AdminUserFilters.jsx
          AdminRoleBadge.jsx
          AdminStatusBadge.jsx
          AdminAnalyticsCharts.jsx
          ReportBuilder.jsx
          ActivityLogTable.jsx
          NotificationComposer.jsx
          ApprovalQueue.jsx
          ContentEditor.jsx
          MonitoringHealthCards.jsx
          SecurityAlertsPanel.jsx
          AdminConfirmDialog.jsx
          AdminEmptyState.jsx
          AdminSkeleton.jsx
        services/
          adminService.js
        hooks/
          useAdminUsers.js
          useAdminAnalytics.js
          useAdminReports.js
          useAdminActivityLogs.js
          useAdminNotifications.js
          useAdminApprovals.js
          useAdminMonitoring.js
        validation/
          adminSchemas.js
        utils/
          adminFormatters.js
```

## 9. UI Design

### Admin Dashboard

The admin dashboard should be dense and operational:

- KPI cards.
- User growth chart.
- Module usage chart.
- Pending approvals queue.
- Recent activity logs.
- Security alerts.
- System health cards.

### User Management UI

Desktop:

- Table-first layout.
- Search and filters.
- Bulk actions in future.
- User details drawer.

Mobile:

- Compact user cards.
- Filter drawer.
- Clear status and role badges.

### Reports UI

- Report type selector.
- Date range filter.
- Generate action.
- Report history table.
- Download action.

### Monitoring UI

- Service health cards.
- Error table.
- Integration status.
- Recent failed jobs.

## 10. Security Considerations

- All admin endpoints require `ADMIN` or `SUPER_ADMIN`.
- Role changes require `SUPER_ADMIN`.
- Admin cannot demote or disable themselves without special safeguards.
- Admin cannot modify a `SUPER_ADMIN` unless actor is `SUPER_ADMIN`.
- Every sensitive action must create an audit log.
- Admin APIs must never expose password hashes, tokens, or secrets.
- Activity logs must redact sensitive data.
- Monitoring endpoints must hide environment secrets.
- Admin actions should require confirmation in UI.
- Future: step-up authentication for destructive operations.

## 11. Validation Rules

```text
status: required enum
role: required enum
reason: required for sensitive user status/role changes, max 500 characters
notification title: required, 3-180 characters
notification message: required, max 5000 characters
targetType: required enum ALL/ROLE/USER_SEGMENT/USER
content title: required, 3-180 characters
content body: optional, max 20000 characters
approval review note: optional, max 500 characters
date range: valid start <= end
```

## 12. Analytics Strategy

Admin analytics should be read-optimized:

- Use aggregate queries.
- Avoid loading full entity lists for counts.
- Cache expensive summaries in future.
- Use date range filters consistently.
- Return chart-ready DTOs.

Recommended charts:

- User growth line chart.
- Module usage bar chart.
- Role distribution pie chart.
- AI usage trend.
- Security events over time.
- Approval status distribution.

## 13. Reporting Strategy

Initial phase:

- Generate reports synchronously for small datasets.
- CSV export for user/report tables.

Future phase:

- Async report generation.
- Report status polling.
- PDF reports.
- Scheduled reports.
- Email delivery to admins.

## 14. Monitoring Strategy

Initial monitoring:

- Spring Boot actuator health.
- Database health.
- Cloudinary configuration status.
- AI provider configuration status.
- Recent backend errors from stored logs or audit events.

Future monitoring:

- Prometheus/Grafana.
- Centralized logs.
- Alert rules.
- Job queue monitoring.
- Uptime checks.

## 15. Future Scalability

The Admin Panel should support:

- Fine-grained permissions.
- Admin teams.
- Bulk user actions.
- Advanced content workflows.
- Scheduled announcements.
- Multi-tenant organization admin.
- Feature flags.
- Billing/subscription management.
- Abuse detection.
- Data retention tools.
- GDPR-style data export/delete workflows.

## 16. Industry Best Practices

- Keep admin APIs separate from user APIs.
- Use method-level security.
- Audit all sensitive admin actions.
- Use pagination everywhere.
- Use explicit `SUPER_ADMIN` checks for privilege changes.
- Never trust frontend role checks.
- Keep monitoring data sanitized.
- Use DTOs and mappers.
- Use confirmation dialogs for destructive actions.
- Keep admin UI utilitarian and scan-friendly.

## 17. Testing Strategy

### Backend Tests

- Non-admin cannot access admin APIs.
- Admin can list users.
- Admin cannot assign roles.
- SUPER_ADMIN can assign admin role.
- User status change creates audit log.
- Force logout revokes sessions.
- Analytics counts are correct.
- Activity logs are paginated.
- Notification creation validates target.
- Approval approve/reject updates status and actor.
- Monitoring endpoint does not expose secrets.

### Frontend Tests

- Admin dashboard renders KPI cards.
- User table search/filter works.
- Role change action hidden for non-SUPER_ADMIN.
- Confirmation dialog appears for sensitive actions.
- Activity log table renders paginated results.
- Notification composer validates fields.
- Approval queue updates after action.
- Monitoring cards render healthy/degraded states.
- Mobile admin user cards remain readable.

## 18. Swagger Documentation

Swagger group:

```text
Admin Panel
```

Document:

- User management.
- Analytics.
- Reports.
- Activity logs.
- Notifications.
- Approvals.
- Content management.
- Monitoring.
- Security actions.

Each endpoint must include:

- Role requirement.
- Request schema.
- Response schema.
- Validation examples.
- Forbidden examples.
- Audit behavior notes for sensitive actions.

## 19. Implementation Order After Approval

1. Define admin-specific entities and enums.
2. Create repositories and analytics query repository.
3. Define DTOs and validation.
4. Implement admin authorization rules.
5. Implement admin user management service.
6. Implement audit logging for admin actions.
7. Implement analytics service.
8. Implement activity log service.
9. Implement notification service.
10. Implement approval service.
11. Implement content management service.
12. Implement monitoring service.
13. Add controllers and Swagger docs.
14. Add backend tests.
15. Build admin dashboard UI.
16. Build user management table.
17. Build analytics and reports UI.
18. Build activity logs UI.
19. Build notifications, approvals, and content UI.
20. Build monitoring and security UI.
21. Complete responsive polish.

## 20. Approval Gate

No Admin Panel implementation should begin until this architecture is approved.

After approval, implementation should begin with admin authorization, user management APIs, audit logging, and admin dashboard analytics.
