# CareerNest Module 7: Internship Application Tracker

## 1. Purpose of the Module

The Internship Application Tracker helps students and freshers manage internship opportunities from discovery to final outcome. Users can track company, role, status, interview dates, notes, deadlines, reminders, timeline events, analytics, search, filters, and dashboard summaries.

This module is a career workflow system, not a simple application list. It should help users avoid missed deadlines, understand their application funnel, and prepare for interviews.

## 2. Functional Requirements

### Create Internship Applications

Users can create an internship application with:

- Company name.
- Role title.
- Internship type.
- Location.
- Remote/hybrid/onsite mode.
- Source platform.
- Job post URL.
- Status.
- Applied date.
- Deadline.
- Interview date.
- Resume used.
- Notes.
- Compensation/stipend, optional.
- Contact person, optional.
- Tags.

### Edit Internship Applications

Users can update all application details. Status changes must create timeline events automatically.

### Delete Internship Applications

Recommended behavior:

- Soft delete by default.
- Preserve historical analytics if needed.
- Exclude deleted applications from normal lists.

### Company

Company details should support:

- Name.
- Website.
- Industry, optional.
- Location, optional.
- Logo URL, future.

Initial implementation can store company fields directly on the application. A normalized `companies` table can be introduced later if company data becomes shared.

### Role

Role information should include:

- Role title.
- Role category.
- Internship type.
- Required skills.
- Job description.

Examples:

- Frontend Developer Intern.
- Backend Developer Intern.
- Full Stack Developer Intern.
- Data Analyst Intern.
- UI/UX Intern.

### Status

Recommended statuses:

```text
SAVED
APPLIED
ASSESSMENT
SCREENING
INTERVIEW_SCHEDULED
INTERVIEW_COMPLETED
OFFER_RECEIVED
ACCEPTED
REJECTED
WITHDRAWN
EXPIRED
```

Status rules:

- Status changes should be tracked.
- Status changes should update timeline.
- Status can drive dashboard analytics.
- Some statuses may require additional metadata, such as interview date or offer details.

### Interview Date

Users can add:

- Interview date/time.
- Interview round.
- Interview mode.
- Meeting link.
- Interviewer/contact.
- Preparation notes.

Interview date should integrate with:

- Dashboard upcoming interviews.
- Notifications/reminders.
- Timeline.

### Notes

Users can add multiple notes per application:

- General notes.
- Follow-up notes.
- Interview preparation notes.
- Recruiter conversation notes.

Each note should include:

- Content.
- Created date.
- Updated date.

### Deadlines

Track important dates:

- Application deadline.
- Assessment deadline.
- Follow-up date.
- Interview date.
- Offer response deadline.

Deadline state:

- Upcoming.
- Due today.
- Overdue.
- Completed.

### Reminder

Users can create reminders for:

- Apply before deadline.
- Follow up.
- Prepare for interview.
- Complete assessment.
- Respond to offer.

Reminder delivery:

- In-app notification initially.
- Email reminders in future.

### Timeline

Each application should have a timeline:

- Application created.
- Status changed.
- Note added.
- Deadline added.
- Reminder created.
- Interview scheduled.
- Interview completed.
- Offer received.
- Application closed.

Timeline events should be immutable audit-style records.

### Analytics

Application analytics should show:

- Total internship applications.
- Active applications.
- Applications by status.
- Interviews scheduled.
- Offers received.
- Rejections.
- Application conversion rate.
- Applications by company.
- Applications by source.
- Applications over time.
- Upcoming deadlines.

### Search

Users can search by:

- Company.
- Role.
- Notes.
- Tags.
- Source platform.
- Required skills.

### Filters

Users can filter by:

- Status.
- Company.
- Role category.
- Internship type.
- Work mode.
- Source platform.
- Deadline range.
- Interview date range.
- Has reminder.
- Has upcoming deadline.
- Has interview scheduled.
- Resume used.

### Dashboard

Dashboard integration should include:

- Active applications count.
- Upcoming interviews.
- Upcoming deadlines.
- Application status chart.
- Application trend chart.
- Recent application activity.
- Notifications for reminders.

## 3. Non-Functional Requirements

- All application records must be user-scoped.
- APIs must be protected by JWT.
- List endpoints must be paginated.
- Search/filter queries must be efficient.
- Status changes must be auditable.
- Reminder logic must be reliable and time-zone aware.
- UI must support dense data without becoming cluttered.
- Mobile layout must use cards instead of wide tables.
- Analytics must be backend-calculated.
- Future support for email reminders and calendar sync should be possible.

## 4. Database Tables

### `internship_applications`

```text
id BIGINT PK
user_id BIGINT FK users(id)
resume_id BIGINT NULL FK resumes(id)
public_id VARCHAR(64) UNIQUE NOT NULL
company_name VARCHAR(160) NOT NULL
company_website VARCHAR(500)
role_title VARCHAR(160) NOT NULL
role_category VARCHAR(80)
internship_type VARCHAR(80)
work_mode VARCHAR(40)
location VARCHAR(160)
source_platform VARCHAR(120)
job_post_url VARCHAR(500)
job_description TEXT
required_skills TEXT
status VARCHAR(60) NOT NULL
applied_date DATE
application_deadline DATETIME
interview_date DATETIME
stipend VARCHAR(80)
contact_name VARCHAR(120)
contact_email VARCHAR(160)
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

Indexes:

```text
idx_internships_user_id
idx_internships_user_status
idx_internships_company_name
idx_internships_role_title
idx_internships_deadline
idx_internships_interview_date
uk_internships_public_id
```

### `internship_application_notes`

```text
id BIGINT PK
application_id BIGINT FK internship_applications(id)
user_id BIGINT FK users(id)
note_type VARCHAR(60)
content TEXT NOT NULL
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

### `internship_application_tags`

```text
id BIGINT PK
application_id BIGINT FK internship_applications(id)
name VARCHAR(60) NOT NULL
created_at DATETIME
```

### `internship_reminders`

```text
id BIGINT PK
application_id BIGINT FK internship_applications(id)
user_id BIGINT FK users(id)
reminder_type VARCHAR(80) NOT NULL
title VARCHAR(160) NOT NULL
reminder_at DATETIME NOT NULL
status VARCHAR(40) NOT NULL
notification_id BIGINT NULL FK notifications(id)
created_at DATETIME
updated_at DATETIME
completed_at DATETIME NULL
cancelled_at DATETIME NULL
```

Reminder statuses:

```text
SCHEDULED
SENT
COMPLETED
CANCELLED
FAILED
```

### `internship_application_timeline`

```text
id BIGINT PK
application_id BIGINT FK internship_applications(id)
user_id BIGINT FK users(id)
event_type VARCHAR(80) NOT NULL
title VARCHAR(160) NOT NULL
description VARCHAR(500)
from_status VARCHAR(60)
to_status VARCHAR(60)
event_at DATETIME NOT NULL
created_at DATETIME
```

### Future Table: `companies`

```text
id BIGINT PK
name VARCHAR(160) UNIQUE NOT NULL
website VARCHAR(500)
industry VARCHAR(120)
logo_url VARCHAR(500)
created_at DATETIME
updated_at DATETIME
```

## 5. API Endpoints

Base path:

```text
/api/v1/internship-applications
```

### Application CRUD

```text
POST   /api/v1/internship-applications
GET    /api/v1/internship-applications
GET    /api/v1/internship-applications/{applicationId}
PUT    /api/v1/internship-applications/{applicationId}
DELETE /api/v1/internship-applications/{applicationId}
PATCH  /api/v1/internship-applications/{applicationId}/status
```

### Notes

```text
POST   /api/v1/internship-applications/{applicationId}/notes
GET    /api/v1/internship-applications/{applicationId}/notes
PUT    /api/v1/internship-applications/{applicationId}/notes/{noteId}
DELETE /api/v1/internship-applications/{applicationId}/notes/{noteId}
```

### Reminders

```text
POST   /api/v1/internship-applications/{applicationId}/reminders
GET    /api/v1/internship-applications/{applicationId}/reminders
PATCH  /api/v1/internship-applications/{applicationId}/reminders/{reminderId}/complete
DELETE /api/v1/internship-applications/{applicationId}/reminders/{reminderId}
```

### Timeline

```text
GET /api/v1/internship-applications/{applicationId}/timeline
```

### Search, Filters, Analytics

```text
GET /api/v1/internship-applications/search
GET /api/v1/internship-applications/filters
GET /api/v1/internship-applications/analytics
GET /api/v1/internship-applications/upcoming-deadlines
GET /api/v1/internship-applications/upcoming-interviews
```

## 6. Query Parameters

```text
GET /api/v1/internship-applications?page=0&size=10&sort=updatedAt,desc
GET /api/v1/internship-applications/search?q=frontend&status=APPLIED
GET /api/v1/internship-applications/search?company=Google&workMode=REMOTE
GET /api/v1/internship-applications/search?deadlineFrom=2026-07-01&deadlineTo=2026-07-31
GET /api/v1/internship-applications/search?hasInterview=true&hasReminder=true
```

## 7. API Contracts

### Create Internship Application Request

```json
{
  "companyName": "Acme Technologies",
  "companyWebsite": "https://acme.example.com",
  "roleTitle": "Frontend Developer Intern",
  "roleCategory": "Frontend",
  "internshipType": "Summer Internship",
  "workMode": "REMOTE",
  "location": "Bengaluru, India",
  "sourcePlatform": "LinkedIn",
  "jobPostUrl": "https://linkedin.com/jobs/example",
  "jobDescription": "Build modern React interfaces.",
  "requiredSkills": ["React", "JavaScript", "Tailwind CSS"],
  "status": "SAVED",
  "appliedDate": null,
  "applicationDeadline": "2026-08-10T18:00:00",
  "interviewDate": null,
  "resumeId": "res_01J0",
  "stipend": "15000 INR/month",
  "contactName": "Priya Mehta",
  "contactEmail": "priya@example.com",
  "tags": ["Frontend", "Remote", "Priority"]
}
```

### Application Response

```json
{
  "id": "app_01J0",
  "companyName": "Acme Technologies",
  "roleTitle": "Frontend Developer Intern",
  "workMode": "REMOTE",
  "status": "SAVED",
  "appliedDate": null,
  "applicationDeadline": "2026-08-10T18:00:00",
  "interviewDate": null,
  "resume": {
    "id": "res_01J0",
    "title": "Frontend Resume"
  },
  "tags": ["Frontend", "Remote", "Priority"],
  "deadlineState": "UPCOMING",
  "createdAt": "2026-07-17T00:00:00Z",
  "updatedAt": "2026-07-17T00:00:00Z"
}
```

### Update Status Request

```json
{
  "status": "INTERVIEW_SCHEDULED",
  "interviewDate": "2026-08-15T10:30:00",
  "note": "Technical interview scheduled with engineering team."
}
```

### Analytics Response

```json
{
  "totalApplications": 24,
  "activeApplications": 14,
  "upcomingInterviews": 3,
  "upcomingDeadlines": 5,
  "offersReceived": 2,
  "rejections": 6,
  "applicationsByStatus": [
    { "status": "APPLIED", "count": 8 },
    { "status": "INTERVIEW_SCHEDULED", "count": 3 },
    { "status": "OFFER_RECEIVED", "count": 2 }
  ],
  "applicationsBySource": [
    { "source": "LinkedIn", "count": 10 },
    { "source": "Company Website", "count": 6 }
  ],
  "applicationsOverTime": [
    { "date": "2026-07-01", "count": 2 }
  ]
}
```

## 8. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/internship/
    controller/
      InternshipApplicationController.java
      InternshipNoteController.java
      InternshipReminderController.java
      InternshipTimelineController.java
      InternshipAnalyticsController.java
    dto/
      CreateInternshipApplicationRequest.java
      UpdateInternshipApplicationRequest.java
      InternshipApplicationResponse.java
      InternshipApplicationListItemResponse.java
      UpdateInternshipStatusRequest.java
      InternshipNoteRequest.java
      InternshipNoteResponse.java
      InternshipReminderRequest.java
      InternshipReminderResponse.java
      InternshipTimelineResponse.java
      InternshipAnalyticsResponse.java
      InternshipSearchCriteria.java
    entity/
      InternshipApplication.java
      InternshipApplicationNote.java
      InternshipApplicationTag.java
      InternshipReminder.java
      InternshipApplicationTimeline.java
      InternshipApplicationStatus.java
      InternshipWorkMode.java
      InternshipReminderStatus.java
    repository/
      InternshipApplicationRepository.java
      InternshipNoteRepository.java
      InternshipTagRepository.java
      InternshipReminderRepository.java
      InternshipTimelineRepository.java
      InternshipQueryRepository.java
    service/
      InternshipApplicationService.java
      InternshipApplicationServiceImpl.java
      InternshipNoteService.java
      InternshipReminderService.java
      InternshipTimelineService.java
      InternshipAnalyticsService.java
    mapper/
      InternshipApplicationMapper.java
```

## 9. Frontend Folder Structure

```text
frontend/
  src/
    features/
      internships/
        pages/
          InternshipApplicationsPage.jsx
          InternshipApplicationDetailsPage.jsx
          CreateInternshipApplicationPage.jsx
          EditInternshipApplicationPage.jsx
        components/
          InternshipApplicationCard.jsx
          InternshipApplicationTable.jsx
          InternshipApplicationForm.jsx
          InternshipStatusBadge.jsx
          InternshipFilters.jsx
          InternshipSearchBar.jsx
          InternshipAnalyticsCards.jsx
          InternshipTimeline.jsx
          InternshipNotesPanel.jsx
          InternshipReminderForm.jsx
          UpcomingDeadlinesCard.jsx
          UpcomingInterviewsCard.jsx
          InternshipDeleteDialog.jsx
          InternshipEmptyState.jsx
          InternshipSkeleton.jsx
        services/
          internshipService.js
        hooks/
          useInternshipApplications.js
          useInternshipDetails.js
          useInternshipAnalytics.js
          useInternshipMutations.js
        validation/
          internshipSchemas.js
        utils/
          internshipFormatters.js
```

## 10. UI Design

### Applications Page

Primary sections:

- Page header with `Add Application`.
- Analytics cards.
- Search bar.
- Filters.
- Desktop table view.
- Mobile card view.
- Pagination.

### Application Card

Show:

- Company.
- Role.
- Status badge.
- Deadline state.
- Interview date.
- Source platform.
- Tags.
- Quick actions.

### Details Page

Show:

- Application summary.
- Status timeline.
- Notes panel.
- Reminders.
- Deadlines.
- Interview information.
- Resume used.
- Edit/delete actions.

### Timeline UI

Timeline items should use:

- Event icon.
- Title.
- Description.
- Timestamp.
- Status transition metadata.

### Responsive Strategy

- Desktop: table-first workflow for scanning.
- Tablet: table or compact cards.
- Mobile: cards, collapsible filters, stacked details sections.

## 11. Security Considerations

- All APIs require authentication.
- Backend derives user id from JWT.
- Users can only access their own applications.
- Application, note, reminder, and timeline lookups must include ownership.
- Reminder creation must validate application ownership.
- Resume attachment must validate resume ownership.
- Do not expose private notes to other users.
- Admin views, if added later, must be separate and permission-protected.

## 12. Validation Rules

```text
companyName: required, 2-160 characters
companyWebsite: optional, valid URL
roleTitle: required, 2-160 characters
roleCategory: optional, max 80 characters
internshipType: optional, max 80 characters
workMode: optional enum
location: optional, max 160 characters
sourcePlatform: optional, max 120 characters
jobPostUrl: optional, valid URL
jobDescription: optional, max 10000 characters
requiredSkills: max 30
status: required enum
appliedDate: optional, cannot be far future
applicationDeadline: optional
interviewDate: optional
contactEmail: optional, valid email
tags: max 10
note content: required, max 5000 characters
reminderAt: required, must be in future for scheduled reminders
```

## 13. Analytics Strategy

### Backend Calculations

- Count all non-deleted applications.
- Count active statuses.
- Count by status.
- Count upcoming interviews.
- Count upcoming deadlines.
- Count offers and rejections.
- Group by source platform.
- Group by company.
- Trend applications by date.

### Dashboard Integration

Dashboard should consume:

- Application status distribution.
- Upcoming interviews.
- Upcoming deadlines.
- Active application count.
- Recent application timeline.

## 14. Reminder Strategy

### Initial Phase

- Store reminders in database.
- Show due reminders in notifications panel.
- Mark reminders sent/complete manually or by scheduled backend job.

### Future Phase

- Email reminders.
- Calendar sync.
- Push notifications.
- Recurring follow-up reminders.

## 15. Future Scalability

This module should support:

- Import jobs from LinkedIn/company URLs.
- Browser extension capture.
- Email parsing for interview invites.
- Calendar integration.
- Offer comparison.
- Company CRM-style tracking.
- AI follow-up message suggestions.
- AI job description skill extraction.
- Public company insights.

## 16. Industry Best Practices

- Treat status changes as events.
- Keep notes separate from application core table.
- Keep reminders separate from notifications.
- Use pagination for list APIs.
- Use DTOs and mappers.
- Enforce ownership in backend service/repository layer.
- Store timeline as immutable records.
- Keep analytics backend-side.
- Make deadline calculations timezone-aware.
- Use dashboard summary endpoints for aggregated widgets.

## 17. Testing Strategy

### Backend Tests

- Create application succeeds with valid data.
- Invalid URL/email is rejected.
- User cannot access another user's application.
- Status update creates timeline event.
- Notes are scoped to owner.
- Reminder creation validates future date.
- Resume ownership is validated.
- Search matches company and role.
- Filters return correct status/deadline/interview results.
- Pagination metadata is correct.
- Analytics counts are correct.

### Frontend Tests

- Application list loading state renders.
- Empty state renders.
- Form validates required fields.
- Status badge renders correct state.
- Filters update query.
- Pagination changes page.
- Timeline renders status changes.
- Reminder form validates future time.
- Mobile card layout remains usable.

## 18. Swagger Documentation

Swagger group:

```text
Internship Applications
```

Document:

- Application CRUD.
- Status update.
- Notes.
- Reminders.
- Timeline.
- Search and filters.
- Analytics.
- Upcoming deadlines.
- Upcoming interviews.

Each endpoint must include:

- Authentication requirement.
- Request schema.
- Response schema.
- Validation examples.
- Ownership error examples.
- Pagination examples.

## 19. Implementation Order After Approval

1. Create internship application, note, tag, reminder, and timeline entities.
2. Create enums.
3. Create repositories and query repository.
4. Define DTOs and validation.
5. Build mapper.
6. Implement application CRUD service.
7. Implement status transition and timeline service.
8. Implement notes service.
9. Implement reminders service.
10. Implement analytics service.
11. Add controllers and Swagger docs.
12. Add backend tests.
13. Build frontend cards/table.
14. Build create/edit form.
15. Build search, filters, and pagination.
16. Build timeline and notes panels.
17. Build reminder UI.
18. Build analytics cards.
19. Integrate dashboard widgets.
20. Complete responsive polish.

## 20. Approval Gate

No Internship Application Tracker implementation should begin until this architecture is approved.

After approval, implementation should begin with backend internship entities, ownership-secure CRUD APIs, status timeline, and reminder foundation.
