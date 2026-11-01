# CareerNest Module 8: Learning Tracker

## 1. Purpose of the Module

The Learning Tracker helps students and freshers plan, track, and improve their learning across core career skills such as Java, React, Spring, SQL, DSA, and Aptitude. It also tracks courses, books, videos, progress, charts, goals, achievements, streaks, and badges.

This module becomes the learning intelligence layer of CareerNest. It feeds dashboard progress, career roadmap completion, resume skill confidence, and future recommendations.

## 2. Functional Requirements

### Track Skill Areas

Default learning tracks:

- Java.
- React.
- Spring.
- SQL.
- DSA.
- Aptitude.

Each track should support:

- Title.
- Category.
- Description.
- Difficulty level.
- Target completion date.
- Progress percentage.
- Active/inactive state.
- Custom tracks in future.

### Courses

Users can add courses with:

- Course title.
- Provider.
- Course URL.
- Skill track.
- Status.
- Start date.
- Completion date.
- Estimated hours.
- Actual hours.
- Notes.

Course statuses:

```text
NOT_STARTED
IN_PROGRESS
COMPLETED
PAUSED
ABANDONED
```

### Books

Users can track books with:

- Title.
- Author.
- Skill track.
- Total pages.
- Pages read.
- Status.
- Notes.

### Videos

Users can track videos/playlists with:

- Title.
- Platform.
- URL.
- Skill track.
- Duration.
- Watched duration.
- Status.
- Notes.

### Progress

Progress should be tracked at multiple levels:

- Track-level progress.
- Resource-level progress.
- Goal-level progress.
- Daily learning progress.
- Weekly learning progress.

Users should be able to update:

- Completion percentage.
- Time spent.
- Completed lessons/topics.
- Notes.

### Charts

Use Recharts for:

- Weekly learning hours.
- Progress by skill track.
- Resource completion breakdown.
- Goal completion trend.
- Streak calendar summary.
- Skill category distribution.

### Goals

Users can create learning goals:

- Goal title.
- Skill track.
- Goal type.
- Target value.
- Current value.
- Due date.
- Status.

Goal examples:

- Complete 30 DSA problems.
- Finish Spring Boot course.
- Read 100 pages of SQL book.
- Watch 10 React videos.
- Study aptitude 5 hours this week.

Goal statuses:

```text
ACTIVE
COMPLETED
MISSED
CANCELLED
```

### Achievements

Achievements are earned when users reach milestones:

- First course completed.
- 7-day streak.
- 30 DSA problems solved.
- Java track 100 percent complete.
- Completed all core tracks.
- Finished first book.

Achievements should be system-generated, not manually created by users.

### Streak

Track daily learning activity:

- Current streak.
- Longest streak.
- Last activity date.
- Active days this month.
- Missed days.

Streak activity can be counted when a user:

- Logs learning time.
- Completes a resource.
- Updates progress.
- Completes a goal.

### Badges

Badges are visual rewards tied to achievements:

- Java Starter.
- React Builder.
- Spring Explorer.
- SQL Analyst.
- DSA Grinder.
- Aptitude Ready.
- Consistency 7.
- Consistency 30.
- Course Finisher.

Badges should include:

- Name.
- Description.
- Icon key.
- Rarity.
- Earned date.

## 3. Non-Functional Requirements

- All learning data must be user-scoped.
- APIs must be protected by JWT.
- Progress calculations must be backend-authoritative.
- Dashboard summaries must be optimized.
- Charts must handle empty data.
- UI must be motivating without becoming childish.
- Streak logic must be timezone-aware.
- Learning activity writes should be auditable.
- List endpoints must support pagination.
- Search and filters must remain fast.

## 4. Database Tables

### `learning_tracks`

Stores learning skill tracks.

```text
id BIGINT PK
user_id BIGINT NULL FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
name VARCHAR(120) NOT NULL
category VARCHAR(80)
description VARCHAR(500)
difficulty_level VARCHAR(40)
is_default BOOLEAN DEFAULT FALSE
is_active BOOLEAN DEFAULT TRUE
created_at DATETIME
updated_at DATETIME
```

Default seeded tracks:

```text
Java
React
Spring
SQL
DSA
Aptitude
```

### `learning_resources`

Stores courses, books, videos, and other resources.

```text
id BIGINT PK
user_id BIGINT FK users(id)
track_id BIGINT FK learning_tracks(id)
public_id VARCHAR(64) UNIQUE NOT NULL
resource_type VARCHAR(40) NOT NULL
title VARCHAR(180) NOT NULL
provider VARCHAR(160)
author VARCHAR(160)
url VARCHAR(500)
total_units INT
completed_units INT DEFAULT 0
estimated_hours DECIMAL(6,2)
actual_hours DECIMAL(6,2) DEFAULT 0
status VARCHAR(40) NOT NULL
start_date DATE
completed_date DATE
notes TEXT
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

Resource types:

```text
COURSE
BOOK
VIDEO
ARTICLE
PRACTICE_SET
OTHER
```

### `learning_goals`

```text
id BIGINT PK
user_id BIGINT FK users(id)
track_id BIGINT NULL FK learning_tracks(id)
public_id VARCHAR(64) UNIQUE NOT NULL
title VARCHAR(180) NOT NULL
goal_type VARCHAR(60) NOT NULL
target_value INT NOT NULL
current_value INT DEFAULT 0
unit VARCHAR(40)
due_date DATE
status VARCHAR(40) NOT NULL
created_at DATETIME
updated_at DATETIME
completed_at DATETIME NULL
deleted_at DATETIME NULL
```

### `learning_activity_logs`

```text
id BIGINT PK
user_id BIGINT FK users(id)
track_id BIGINT NULL FK learning_tracks(id)
resource_id BIGINT NULL FK learning_resources(id)
goal_id BIGINT NULL FK learning_goals(id)
activity_type VARCHAR(60) NOT NULL
activity_date DATE NOT NULL
duration_minutes INT DEFAULT 0
progress_delta INT DEFAULT 0
description VARCHAR(500)
created_at DATETIME
```

### `learning_streaks`

```text
id BIGINT PK
user_id BIGINT UNIQUE FK users(id)
current_streak INT DEFAULT 0
longest_streak INT DEFAULT 0
last_activity_date DATE
active_days_this_month INT DEFAULT 0
updated_at DATETIME
```

### `badges`

```text
id BIGINT PK
badge_key VARCHAR(80) UNIQUE NOT NULL
name VARCHAR(120) NOT NULL
description VARCHAR(255)
icon_key VARCHAR(80)
rarity VARCHAR(40)
is_active BOOLEAN DEFAULT TRUE
created_at DATETIME
updated_at DATETIME
```

### `user_badges`

```text
id BIGINT PK
user_id BIGINT FK users(id)
badge_id BIGINT FK badges(id)
earned_at DATETIME
source_event VARCHAR(80)
```

### `learning_achievements`

```text
id BIGINT PK
user_id BIGINT FK users(id)
achievement_key VARCHAR(80) NOT NULL
title VARCHAR(160) NOT NULL
description VARCHAR(500)
earned_at DATETIME
created_at DATETIME
```

## 5. API Endpoints

Base path:

```text
/api/v1/learning
```

### Tracks

```text
GET    /api/v1/learning/tracks
POST   /api/v1/learning/tracks
GET    /api/v1/learning/tracks/{trackId}
PUT    /api/v1/learning/tracks/{trackId}
DELETE /api/v1/learning/tracks/{trackId}
```

### Resources

```text
POST   /api/v1/learning/resources
GET    /api/v1/learning/resources
GET    /api/v1/learning/resources/{resourceId}
PUT    /api/v1/learning/resources/{resourceId}
DELETE /api/v1/learning/resources/{resourceId}
PATCH  /api/v1/learning/resources/{resourceId}/progress
```

### Goals

```text
POST   /api/v1/learning/goals
GET    /api/v1/learning/goals
GET    /api/v1/learning/goals/{goalId}
PUT    /api/v1/learning/goals/{goalId}
DELETE /api/v1/learning/goals/{goalId}
PATCH  /api/v1/learning/goals/{goalId}/progress
PATCH  /api/v1/learning/goals/{goalId}/complete
```

### Activity, Streak, Badges

```text
POST /api/v1/learning/activity
GET  /api/v1/learning/activity
GET  /api/v1/learning/streak
GET  /api/v1/learning/badges
GET  /api/v1/learning/achievements
```

### Analytics and Dashboard

```text
GET /api/v1/learning/analytics
GET /api/v1/learning/charts/progress-by-track
GET /api/v1/learning/charts/weekly-hours
GET /api/v1/learning/charts/resource-completion
GET /api/v1/learning/dashboard-summary
```

## 6. API Contracts

### Create Learning Resource Request

```json
{
  "trackId": "trk_java",
  "resourceType": "COURSE",
  "title": "Java Masterclass",
  "provider": "Udemy",
  "url": "https://example.com/java-course",
  "totalUnits": 50,
  "completedUnits": 10,
  "estimatedHours": 40,
  "actualHours": 8,
  "status": "IN_PROGRESS",
  "startDate": "2026-07-01",
  "notes": "Focus on OOP and collections first."
}
```

### Update Resource Progress Request

```json
{
  "completedUnits": 20,
  "actualHours": 14,
  "activityMinutes": 90,
  "note": "Completed exception handling and collections modules."
}
```

### Create Goal Request

```json
{
  "trackId": "trk_dsa",
  "title": "Solve 30 array problems",
  "goalType": "PRACTICE_COUNT",
  "targetValue": 30,
  "currentValue": 5,
  "unit": "problems",
  "dueDate": "2026-08-15"
}
```

### Analytics Response

```json
{
  "totalTracks": 6,
  "activeResources": 8,
  "completedResources": 4,
  "activeGoals": 5,
  "completedGoals": 3,
  "currentStreak": 7,
  "longestStreak": 18,
  "badgesEarned": 6,
  "progressByTrack": [
    { "track": "Java", "progress": 65 },
    { "track": "React", "progress": 48 },
    { "track": "DSA", "progress": 35 }
  ],
  "weeklyHours": [
    { "date": "2026-07-13", "hours": 1.5 }
  ]
}
```

## 7. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/learning/
    controller/
      LearningTrackController.java
      LearningResourceController.java
      LearningGoalController.java
      LearningActivityController.java
      LearningAnalyticsController.java
      LearningBadgeController.java
    dto/
      CreateLearningTrackRequest.java
      LearningTrackResponse.java
      CreateLearningResourceRequest.java
      UpdateLearningResourceRequest.java
      UpdateResourceProgressRequest.java
      LearningResourceResponse.java
      CreateLearningGoalRequest.java
      UpdateLearningGoalRequest.java
      UpdateGoalProgressRequest.java
      LearningGoalResponse.java
      LearningActivityRequest.java
      LearningActivityResponse.java
      LearningAnalyticsResponse.java
      LearningStreakResponse.java
      BadgeResponse.java
      AchievementResponse.java
    entity/
      LearningTrack.java
      LearningResource.java
      LearningGoal.java
      LearningActivityLog.java
      LearningStreak.java
      Badge.java
      UserBadge.java
      LearningAchievement.java
      LearningResourceType.java
      LearningStatus.java
      LearningGoalStatus.java
    repository/
      LearningTrackRepository.java
      LearningResourceRepository.java
      LearningGoalRepository.java
      LearningActivityLogRepository.java
      LearningStreakRepository.java
      BadgeRepository.java
      UserBadgeRepository.java
      LearningAchievementRepository.java
      LearningQueryRepository.java
    service/
      LearningTrackService.java
      LearningResourceService.java
      LearningGoalService.java
      LearningActivityService.java
      LearningStreakService.java
      LearningBadgeService.java
      LearningAnalyticsService.java
    mapper/
      LearningMapper.java
```

## 8. Frontend Folder Structure

```text
frontend/
  src/
    features/
      learning/
        pages/
          LearningDashboardPage.jsx
          LearningTracksPage.jsx
          LearningResourcesPage.jsx
          LearningGoalsPage.jsx
          LearningAchievementsPage.jsx
        components/
          LearningTrackCard.jsx
          LearningProgressChart.jsx
          WeeklyHoursChart.jsx
          ResourceCompletionChart.jsx
          LearningResourceCard.jsx
          LearningResourceForm.jsx
          LearningGoalCard.jsx
          LearningGoalForm.jsx
          StreakCard.jsx
          BadgeGrid.jsx
          AchievementList.jsx
          LearningFilters.jsx
          LearningSearchBar.jsx
          LearningAnalyticsCards.jsx
          LearningEmptyState.jsx
          LearningSkeleton.jsx
        services/
          learningService.js
        hooks/
          useLearningAnalytics.js
          useLearningTracks.js
          useLearningResources.js
          useLearningGoals.js
          useLearningBadges.js
        validation/
          learningSchemas.js
        utils/
          learningFormatters.js
```

## 9. UI Design

### Learning Dashboard

Sections:

- Progress summary cards.
- Current streak card.
- Progress by track chart.
- Weekly learning hours chart.
- Active goals.
- Recent resources.
- Badges and achievements.

### Track Cards

Each track card shows:

- Track name.
- Category.
- Progress percentage.
- Active resources.
- Active goals.
- Last activity.
- Continue action.

### Resource Cards

Show:

- Resource type.
- Title.
- Provider/author/platform.
- Track.
- Status.
- Progress bar.
- Hours.
- Continue/edit actions.

### Goals UI

Show:

- Goal title.
- Track.
- Current vs target.
- Due date.
- Status badge.
- Progress controls.

### Badges UI

Use restrained professional badge styling:

- Icon.
- Name.
- Description.
- Earned date.
- Locked/unlocked state.

### Responsive Strategy

- Desktop: chart-first dashboard with multi-column cards.
- Tablet: two-column card grids.
- Mobile: single-column layout, compact charts, simplified filters.

## 10. Security Considerations

- All APIs require authentication.
- Backend derives user id from JWT.
- Users can only access their own learning data.
- Default tracks can be globally readable, but user progress is private.
- Progress updates must validate resource/goal ownership.
- Badge awarding must be backend-controlled.
- Streak updates must not trust arbitrary client dates without validation.

## 11. Validation Rules

```text
track name: required, 2-120 characters
resource title: required, 2-180 characters
resource URL: optional, valid URL
resource type: required enum
totalUnits: optional, positive integer
completedUnits: 0 <= completedUnits <= totalUnits
estimatedHours: optional, positive
actualHours: optional, non-negative
goal title: required, 2-180 characters
targetValue: required, positive integer
currentValue: 0 <= currentValue <= targetValue
dueDate: optional, cannot be too far in past for active goals
activityMinutes: optional, 0-1440
```

## 12. Analytics Strategy

Backend should calculate:

- Track progress from resources and goals.
- Weekly hours from activity logs.
- Current and longest streak.
- Completed resources by type.
- Active and completed goals.
- Earned badges.
- Learning activity trend.

Dashboard integration:

- Learning progress card.
- Weekly activity chart.
- Streak summary.
- Recent achievements.
- Active goals.

## 13. Badge and Achievement Rules

Initial badge rules:

```text
JAVA_STARTER: Add first Java resource
REACT_BUILDER: Complete one React resource
SPRING_EXPLORER: Reach 50% Spring progress
SQL_ANALYST: Complete one SQL goal
DSA_GRINDER: Complete 30 DSA practice units
APTITUDE_READY: Complete 10 aptitude sessions
CONSISTENCY_7: Maintain 7-day streak
CONSISTENCY_30: Maintain 30-day streak
COURSE_FINISHER: Complete first course
BOOK_FINISHER: Complete first book
```

Achievements should be awarded idempotently. A user should not receive the same badge multiple times unless the badge is explicitly repeatable.

## 14. Future Scalability

This module should support:

- AI-generated learning plans.
- Role-based skill paths.
- Integration with YouTube playlists.
- GitHub coding activity integration.
- LeetCode/HackerRank integration.
- Calendar scheduling.
- Team/class cohorts.
- Public learning profile.
- Premium guided roadmaps.
- Adaptive recommendations based on weak areas.

## 15. Industry Best Practices

- Keep activity logs append-only.
- Calculate streaks server-side.
- Award badges through backend rules.
- Store structured resources instead of free text only.
- Use chart DTOs rather than raw records.
- Use pagination for resource/activity lists.
- Keep motivation UI professional.
- Avoid making frontend responsible for business progress calculations.
- Use scheduled jobs later for missed goals and reminders.

## 16. Testing Strategy

### Backend Tests

- Default tracks are available.
- User can create custom track.
- Resource progress updates correctly.
- Completed units cannot exceed total units.
- Goal progress updates correctly.
- Completing a goal creates activity.
- Streak increments for consecutive activity days.
- Streak resets after missed day.
- Badge is awarded once.
- Analytics returns correct progress by track.
- User cannot access another user's resources.

### Frontend Tests

- Learning dashboard renders analytics cards.
- Empty state renders when no resources exist.
- Resource form validates required fields.
- Progress update validates bounds.
- Goal form validates target/current values.
- Charts handle empty data.
- Badge grid renders locked and earned badges.
- Mobile layout remains readable.

## 17. Swagger Documentation

Swagger group:

```text
Learning Tracker
```

Document:

- Tracks.
- Resources.
- Goals.
- Activity.
- Streaks.
- Badges.
- Achievements.
- Analytics.
- Chart endpoints.

Each endpoint must include:

- Authentication requirement.
- Request schema.
- Response schema.
- Validation errors.
- Ownership error examples.
- Pagination examples.

## 18. Implementation Order After Approval

1. Create learning entities and enums.
2. Seed default tracks and badges.
3. Create repositories and query repository.
4. Define DTOs and validation rules.
5. Build learning mapper.
6. Implement track service.
7. Implement resource service.
8. Implement goal service.
9. Implement activity and streak service.
10. Implement badge and achievement service.
11. Implement analytics service.
12. Add controllers and Swagger docs.
13. Add backend tests.
14. Build frontend learning dashboard.
15. Build track/resource/goal cards and forms.
16. Build charts with Recharts.
17. Build streak, badges, and achievements UI.
18. Integrate dashboard widgets.
19. Complete responsive polish.

## 19. Approval Gate

No Learning Tracker implementation should begin until this architecture is approved.

After approval, implementation should begin with backend learning entities, default track and badge seeding, secure progress tracking APIs, and analytics foundation.
