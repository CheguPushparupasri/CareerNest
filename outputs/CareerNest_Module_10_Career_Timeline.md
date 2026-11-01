# CareerNest Module 10: Career Timeline

## 1. Purpose of the Module

The Career Timeline module gives users a visual, interactive story of their career journey. It combines manually added milestones with automatically generated events from other CareerNest modules such as projects, internships, certifications, learning, achievements, hackathons, experience, and placements.

The timeline helps students and freshers understand their progress, showcase growth, prepare for interviews, and export a polished career story for portfolios or placement discussions.

## 2. Functional Requirements

### Timeline Milestones

Users can track milestones such as:

- Joined college.
- Projects.
- Internships.
- Certificates.
- Hackathons.
- Placements.
- Achievements.
- Learning progress.
- Experience.

Each milestone should support:

- Title.
- Description.
- Event type.
- Event date.
- Optional start and end date.
- Source module.
- Linked entity.
- Visibility.
- Attachments or image URL.
- Tags.
- Highlight flag.

### Joined College

Users can add college start as a major timeline milestone:

- Institution name.
- Degree.
- Field of study.
- Joining date.
- Expected graduation date.
- Description.

This can be linked to the Education/Profile module later.

### Projects

Project timeline events can be auto-generated when:

- Project is created.
- Project is marked completed.
- Project is marked featured.
- Project receives major update.

Users can also manually add project milestones.

### Internships

Internship timeline events can be generated when:

- Internship application is submitted.
- Interview is scheduled.
- Offer is received.
- Internship is accepted.
- Internship starts.
- Internship ends.

### Certificates

Certification events can be generated when:

- Certificate is uploaded.
- Certificate is verified.
- Certificate is expiring soon.
- Certificate expires.

### Hackathons

Users can manually add hackathon milestones:

- Hackathon name.
- Organizer.
- Team name.
- Role.
- Result.
- Project built.
- Event date.
- Certificate or proof URL.

Future enhancement: link hackathons to Projects and Certifications.

### Placements

Placement events can track:

- Placement registration.
- Company shortlist.
- Assessment.
- Interview rounds.
- Offer received.
- Offer accepted.

Future enhancement: integrate with Internship/Application Tracker or a dedicated Placement module.

### Achievements

Achievement events can include:

- Awards.
- Badges.
- Coding milestones.
- Learning milestones.
- Competition wins.
- Publications.
- Leadership roles.

### Learning

Learning timeline events can be generated when:

- Course completed.
- Book completed.
- Goal completed.
- Badge earned.
- Streak milestone reached.
- Track completed.

### Experience

Experience timeline events can include:

- Internship started.
- Internship completed.
- Freelance work.
- Part-time role.
- Open-source contribution.
- Volunteer experience.

### Interactive Timeline

The UI should support:

- Vertical timeline on desktop.
- Compact vertical timeline on mobile.
- Expand/collapse event details.
- Click event to open linked detail page.
- Highlight key milestones.
- Date grouping by month/year.
- Timeline density controls in future.

### Animations

Use Framer Motion for:

- Timeline item entrance animation.
- Expand/collapse transitions.
- Filter transitions.
- Highlight pulse for important upcoming milestones.

Animations must be subtle and professional.

### Filtering

Users can filter by:

- Event type.
- Source module.
- Date range.
- Highlighted only.
- Visibility.
- Tags.
- Search text.

### Export Timeline

Users can export timeline as:

- PDF.
- Image, future.
- JSON data export, future.

PDF export should support:

- Chronological timeline.
- User name and headline.
- Selected date range.
- Selected event types.
- Clean print layout.

## 3. Non-Functional Requirements

- Timeline data must be user-scoped.
- Auto-generated events should be idempotent.
- Timeline queries must support pagination or cursor loading.
- Filtering must remain fast as events grow.
- Event source linkage must be flexible.
- Export must not expose private/internal IDs.
- Timeline UI must be responsive and accessible.
- Animations must not block usability or create layout instability.
- Dashboard should be able to fetch recent timeline events efficiently.

## 4. Database Tables

### `career_timeline_events`

Central table for timeline milestones.

```text
id BIGINT PK
user_id BIGINT FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
event_type VARCHAR(80) NOT NULL
source_module VARCHAR(80)
source_entity_type VARCHAR(80)
source_entity_id BIGINT NULL
title VARCHAR(180) NOT NULL
description TEXT
event_date DATETIME NOT NULL
start_date DATE NULL
end_date DATE NULL
location VARCHAR(160)
image_url VARCHAR(500)
attachment_url VARCHAR(500)
external_url VARCHAR(500)
visibility VARCHAR(40) DEFAULT 'PRIVATE'
highlighted BOOLEAN DEFAULT FALSE
metadata_json JSON
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

Recommended event types:

```text
COLLEGE_JOINED
PROJECT_CREATED
PROJECT_COMPLETED
INTERNSHIP_APPLIED
INTERNSHIP_STARTED
INTERNSHIP_COMPLETED
CERTIFICATE_ADDED
CERTIFICATE_VERIFIED
HACKATHON_PARTICIPATED
HACKATHON_WON
PLACEMENT_SHORTLISTED
PLACEMENT_OFFER_RECEIVED
PLACEMENT_ACCEPTED
ACHIEVEMENT_EARNED
LEARNING_GOAL_COMPLETED
COURSE_COMPLETED
BADGE_EARNED
EXPERIENCE_STARTED
EXPERIENCE_COMPLETED
CUSTOM
```

Indexes:

```text
idx_timeline_user_event_date
idx_timeline_user_event_type
idx_timeline_user_source
idx_timeline_user_highlighted
uk_timeline_public_id
```

### `career_timeline_tags`

```text
id BIGINT PK
timeline_event_id BIGINT FK career_timeline_events(id)
name VARCHAR(60) NOT NULL
created_at DATETIME
```

Indexes:

```text
idx_timeline_tags_event_id
idx_timeline_tags_name
```

### `career_timeline_exports`

Tracks exported files.

```text
id BIGINT PK
user_id BIGINT FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
export_type VARCHAR(40) NOT NULL
file_url VARCHAR(500)
file_public_id VARCHAR(255)
filters_json JSON
export_status VARCHAR(40) NOT NULL
created_at DATETIME
completed_at DATETIME NULL
```

## 5. API Endpoints

Base path:

```text
/api/v1/career-timeline
```

### Timeline CRUD

```text
POST   /api/v1/career-timeline/events
GET    /api/v1/career-timeline/events
GET    /api/v1/career-timeline/events/{eventId}
PUT    /api/v1/career-timeline/events/{eventId}
DELETE /api/v1/career-timeline/events/{eventId}
PATCH  /api/v1/career-timeline/events/{eventId}/highlight
```

### Filtering and Summary

```text
GET /api/v1/career-timeline/events/search
GET /api/v1/career-timeline/filters
GET /api/v1/career-timeline/summary
GET /api/v1/career-timeline/recent
```

### Export

```text
POST /api/v1/career-timeline/export/pdf
GET  /api/v1/career-timeline/exports
GET  /api/v1/career-timeline/exports/{exportId}
```

### Internal Event Creation

These should be service methods, not public APIs:

```text
createTimelineEventForProject(...)
createTimelineEventForCertification(...)
createTimelineEventForInternship(...)
createTimelineEventForLearning(...)
createTimelineEventForBadge(...)
```

## 6. Query Parameters

```text
GET /api/v1/career-timeline/events?page=0&size=20&sort=eventDate,desc
GET /api/v1/career-timeline/events/search?q=react&type=PROJECT_COMPLETED
GET /api/v1/career-timeline/events/search?from=2026-01-01&to=2026-12-31
GET /api/v1/career-timeline/events/search?highlighted=true&tag=Full%20Stack
```

## 7. API Contracts

### Create Timeline Event Request

```json
{
  "eventType": "HACKATHON_WON",
  "title": "Won Smart India Hackathon College Round",
  "description": "Built a student career management prototype with a team of four.",
  "eventDate": "2026-03-12T10:00:00",
  "startDate": "2026-03-10",
  "endDate": "2026-03-12",
  "location": "Bengaluru, India",
  "externalUrl": "https://example.com/hackathon",
  "visibility": "PRIVATE",
  "highlighted": true,
  "tags": ["Hackathon", "Team Project", "Achievement"],
  "metadata": {
    "organizer": "College Innovation Cell",
    "result": "Winner"
  }
}
```

### Timeline Event Response

```json
{
  "id": "tl_01J0",
  "eventType": "HACKATHON_WON",
  "sourceModule": "MANUAL",
  "title": "Won Smart India Hackathon College Round",
  "description": "Built a student career management prototype with a team of four.",
  "eventDate": "2026-03-12T10:00:00",
  "startDate": "2026-03-10",
  "endDate": "2026-03-12",
  "location": "Bengaluru, India",
  "visibility": "PRIVATE",
  "highlighted": true,
  "tags": ["Hackathon", "Team Project", "Achievement"],
  "linkedEntity": null,
  "createdAt": "2026-07-17T00:00:00Z",
  "updatedAt": "2026-07-17T00:00:00Z"
}
```

### Timeline Summary Response

```json
{
  "totalEvents": 42,
  "highlightedEvents": 8,
  "eventsByType": [
    { "eventType": "PROJECT_COMPLETED", "count": 6 },
    { "eventType": "CERTIFICATE_ADDED", "count": 5 },
    { "eventType": "LEARNING_GOAL_COMPLETED", "count": 12 }
  ],
  "latestEvent": {
    "title": "Completed Spring Boot Course",
    "eventDate": "2026-07-15T00:00:00"
  }
}
```

### Export Request

```json
{
  "fromDate": "2025-01-01",
  "toDate": "2026-12-31",
  "eventTypes": ["PROJECT_COMPLETED", "CERTIFICATE_ADDED", "HACKATHON_WON"],
  "highlightedOnly": false,
  "includePrivateNotes": false
}
```

## 8. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/timeline/
    controller/
      CareerTimelineController.java
      CareerTimelineExportController.java
    dto/
      CreateTimelineEventRequest.java
      UpdateTimelineEventRequest.java
      TimelineEventResponse.java
      TimelineSearchCriteria.java
      TimelineSummaryResponse.java
      TimelineExportRequest.java
      TimelineExportResponse.java
    entity/
      CareerTimelineEvent.java
      CareerTimelineTag.java
      CareerTimelineExport.java
      TimelineEventType.java
      TimelineVisibility.java
      TimelineExportStatus.java
    repository/
      CareerTimelineRepository.java
      CareerTimelineTagRepository.java
      CareerTimelineExportRepository.java
      CareerTimelineQueryRepository.java
    service/
      CareerTimelineService.java
      CareerTimelineServiceImpl.java
      TimelineEventFactoryService.java
      CareerTimelineExportService.java
      CareerTimelineSummaryService.java
    mapper/
      CareerTimelineMapper.java
```

## 9. Frontend Folder Structure

```text
frontend/
  src/
    features/
      timeline/
        pages/
          CareerTimelinePage.jsx
          TimelineEventDetailsPage.jsx
        components/
          TimelineHeader.jsx
          TimelineView.jsx
          TimelineEventCard.jsx
          TimelineEventForm.jsx
          TimelineFilters.jsx
          TimelineSearchBar.jsx
          TimelineYearGroup.jsx
          TimelineExportDialog.jsx
          TimelineSummaryCards.jsx
          TimelineTypeBadge.jsx
          TimelineEmptyState.jsx
          TimelineSkeleton.jsx
        services/
          timelineService.js
        hooks/
          useTimelineEvents.js
          useTimelineSummary.js
          useTimelineExport.js
        validation/
          timelineSchemas.js
        utils/
          timelineFormatters.js
          timelineEventConfig.js
```

## 10. UI Design

### Career Timeline Page

Primary sections:

- Header with title and `Add Event` / `Export` actions.
- Summary cards.
- Search and filters.
- Interactive timeline.
- Pagination or load more.

### Timeline Event Card

Each card should show:

- Event icon.
- Event type badge.
- Title.
- Date.
- Description preview.
- Source module.
- Tags.
- Highlight marker.
- Linked entity action.
- Edit/delete actions for manual events.

### Interactive Timeline

Desktop:

- Vertical timeline with alternating or single-side event cards.
- Year/month grouping.
- Sticky year markers optional.

Mobile:

- Single-column vertical timeline.
- Compact event cards.
- Filter drawer.

### Animations

Use Framer Motion for:

- Event card fade/slide on load.
- Expand/collapse detail.
- Filtered list transitions.
- Export dialog transition.

Keep animation durations short: 150ms to 250ms.

## 11. Security Considerations

- All timeline APIs require authentication.
- Backend derives user id from JWT.
- Users can only access their own events.
- Linked source entity must validate ownership.
- Manual event URLs must be validated.
- Export must only include the authenticated user's data.
- Public sharing should be future-only and explicit.
- Internal event creation should be idempotent to avoid duplicates.

## 12. Validation Rules

```text
eventType: required enum
title: required, 3-180 characters
description: optional, max 5000 characters
eventDate: required
startDate: optional
endDate: optional, must be after startDate
location: optional, max 160 characters
externalUrl: optional, valid URL
visibility: required enum PRIVATE/PUBLIC
tags: max 10
tag name: 1-60 characters
highlighted: boolean
```

## 13. Export Strategy

### PDF Export

Recommended backend export:

- Generate clean HTML timeline.
- Convert to PDF.
- Store exported file in Cloudinary.
- Return export status and file URL.

PDF layout should include:

- User name.
- Headline.
- Date range.
- Timeline events grouped by year.
- Highlighted events visually emphasized.

### Future Export Options

- PNG image export.
- Public share link.
- JSON export.
- Portfolio embed.

## 14. Dashboard Integration

Dashboard should show:

- Recent timeline events.
- Highlighted career milestones.
- Timeline summary count.
- Upcoming timeline events, if future-dated milestones exist.

Modules that should create timeline events:

- Projects.
- Certifications.
- Internship Applications.
- Learning Tracker.
- Coding Profiles.
- Resume Builder, for resume exported or primary resume set.

## 15. Future Scalability

The Career Timeline should support:

- Public portfolio timeline.
- Recruiter-friendly timeline view.
- AI-generated career story summary.
- Timeline sharing link.
- Drag-and-drop timeline reordering for same-date events.
- Import from LinkedIn.
- Calendar-style view.
- Timeline comments/review for mentors.

## 16. Industry Best Practices

- Use source module and source entity references for traceability.
- Keep auto-generated events idempotent.
- Use soft delete.
- Keep timeline event creation inside domain services.
- Avoid duplicating full source entity data unless needed for snapshots.
- Use DTOs and mappers.
- Paginate timeline events.
- Keep export asynchronous-ready.
- Make filters composable.

## 17. Testing Strategy

### Backend Tests

- Create manual timeline event succeeds.
- Invalid date range is rejected.
- User cannot access another user's event.
- Search filters by type, date, and tag.
- Highlight toggle updates event.
- Soft delete excludes event from normal list.
- Auto-event creation is idempotent.
- Export request validates ownership and filters.
- Summary counts are correct.

### Frontend Tests

- Timeline renders grouped events.
- Empty state renders when no events exist.
- Filters update event list.
- Event card expand/collapse works.
- Add event form validates required fields.
- Export dialog submits selected filters.
- Mobile layout remains readable.
- Animations do not hide content.

## 18. Swagger Documentation

Swagger group:

```text
Career Timeline
```

Document:

- Event CRUD.
- Search/filter endpoint.
- Summary endpoint.
- Recent events endpoint.
- Highlight toggle.
- Export PDF.
- Export history.

Each endpoint must include:

- Authentication requirement.
- Request schema.
- Response schema.
- Validation examples.
- Ownership error examples.
- Pagination examples.

## 19. Implementation Order After Approval

1. Create timeline entities and enums.
2. Create repositories and query repository.
3. Define DTOs and validation.
4. Build mapper.
5. Implement timeline CRUD service.
6. Implement filtering and summary service.
7. Implement internal event factory service.
8. Implement export service.
9. Add controllers and Swagger docs.
10. Add backend tests.
11. Build frontend timeline page.
12. Build event cards and form.
13. Build filters and search.
14. Add Framer Motion interactions.
15. Build export dialog.
16. Integrate dashboard recent events.
17. Complete responsive polish.

## 20. Approval Gate

No Career Timeline implementation should begin until this architecture is approved.

After approval, implementation should begin with backend timeline entities, secure event CRUD APIs, filtering, and internal event factory integration.
