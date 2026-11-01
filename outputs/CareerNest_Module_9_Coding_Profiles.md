# CareerNest Module 9: Coding Profiles

## 1. Purpose of the Module

The Coding Profiles module allows users to manage competitive programming and developer platform profiles in one place. It supports GitHub, LeetCode, CodeChef, HackerRank, and Codeforces, and provides statistics, graphs, ranking, activity tracking, and dashboard integration.

This module helps students and freshers present measurable technical progress, connect coding practice with learning goals, and use coding activity as career evidence for resumes, profiles, and applications.

## 2. Functional Requirements

### Manage Coding Profiles

Users can add, edit, delete, and verify profile links or usernames for:

- GitHub.
- LeetCode.
- CodeChef.
- HackerRank.
- Codeforces.

Each profile should store:

- Platform.
- Username.
- Profile URL.
- Display name.
- Verification status.
- Last synced time.
- Visibility setting.

### GitHub

Track:

- Public repositories.
- Followers.
- Following.
- Contributions.
- Stars received.
- Forks.
- Primary languages.
- Recent repositories.
- Recent activity.

Future enhancements:

- OAuth connection.
- Contribution calendar sync.
- Repository quality score.
- Project import into Projects module.

### LeetCode

Track:

- Total solved.
- Easy solved.
- Medium solved.
- Hard solved.
- Acceptance rate.
- Ranking.
- Contest rating, if available.
- Recent submissions, if available.

### CodeChef

Track:

- Rating.
- Stars.
- Global rank.
- Country rank.
- Problems solved.
- Contests participated.

### HackerRank

Track:

- Badges.
- Skills.
- Certificates.
- Stars or rank where available.
- Problem-solving progress.

### Codeforces

Track:

- Rating.
- Max rating.
- Rank.
- Max rank.
- Contribution.
- Friends count, optional.
- Contest participation.
- Problems solved, if available.

### Statistics

Show consolidated stats:

- Total platforms connected.
- Total problems solved.
- Best competitive rank.
- Highest rating.
- GitHub repositories.
- GitHub contributions.
- Active days.
- Recent activity count.

### Graphs

Use Recharts for:

- Problems solved by platform.
- Difficulty distribution.
- Rating trend.
- Activity over time.
- Platform comparison.
- GitHub language distribution.

### Ranking

Display platform rankings:

- LeetCode ranking.
- CodeChef global/country rank.
- Codeforces rank/rating.
- HackerRank badges/rank where supported.

Ranking should be shown with platform-specific context because different platforms use different ranking models.

### Activity

Track activity events:

- GitHub contribution.
- Repository created.
- Problem solved.
- Contest participated.
- Rating changed.
- Badge earned.
- Profile synced.

Activity can be imported from integrations or manually refreshed depending on implementation phase.

### Dashboard

Dashboard integration should include:

- Connected profiles count.
- Total coding problems solved.
- GitHub contribution summary.
- Rating/rank snapshot.
- Recent coding activity.
- Coding progress chart.

## 3. Non-Functional Requirements

- All profile records must be user-scoped.
- APIs must be protected by JWT.
- External sync must be resilient to platform downtime.
- Manual profile entry must work even before full API integration.
- Statistics must be cached or snapshotted to avoid slow dashboard loads.
- Platform-specific failures must not break the whole dashboard.
- UI must clearly show last synced time.
- Sensitive tokens, if OAuth is added later, must be encrypted at rest.
- Rate limits must be respected for platform APIs.

## 4. Database Tables

### `coding_profiles`

Stores connected or manually added platform profiles.

```text
id BIGINT PK
user_id BIGINT FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
platform VARCHAR(60) NOT NULL
username VARCHAR(120) NOT NULL
profile_url VARCHAR(500)
display_name VARCHAR(160)
verification_status VARCHAR(40) NOT NULL
visibility VARCHAR(40) DEFAULT 'PRIVATE'
last_synced_at DATETIME NULL
sync_status VARCHAR(40)
sync_error VARCHAR(500)
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

Platforms:

```text
GITHUB
LEETCODE
CODECHEF
HACKERRANK
CODEFORCES
```

Verification statuses:

```text
UNVERIFIED
PENDING
VERIFIED
FAILED
```

### `coding_profile_stats`

Stores latest statistics snapshot.

```text
id BIGINT PK
profile_id BIGINT FK coding_profiles(id)
stats_json JSON NOT NULL
total_problems_solved INT DEFAULT 0
easy_solved INT DEFAULT 0
medium_solved INT DEFAULT 0
hard_solved INT DEFAULT 0
rating INT NULL
max_rating INT NULL
rank_label VARCHAR(80)
global_rank INT NULL
country_rank INT NULL
repositories_count INT DEFAULT 0
contributions_count INT DEFAULT 0
followers_count INT DEFAULT 0
badges_count INT DEFAULT 0
captured_at DATETIME NOT NULL
created_at DATETIME
```

### `coding_activity_events`

Stores normalized activity events.

```text
id BIGINT PK
user_id BIGINT FK users(id)
profile_id BIGINT FK coding_profiles(id)
platform VARCHAR(60) NOT NULL
event_type VARCHAR(80) NOT NULL
title VARCHAR(180) NOT NULL
description VARCHAR(500)
event_url VARCHAR(500)
event_date DATETIME NOT NULL
metadata_json JSON
created_at DATETIME
```

### `coding_rating_history`

Stores rating changes over time.

```text
id BIGINT PK
profile_id BIGINT FK coding_profiles(id)
platform VARCHAR(60) NOT NULL
rating INT NOT NULL
rank_label VARCHAR(80)
contest_name VARCHAR(180)
contest_date DATETIME
created_at DATETIME
```

### `coding_language_stats`

Mostly for GitHub.

```text
id BIGINT PK
profile_id BIGINT FK coding_profiles(id)
language VARCHAR(80) NOT NULL
usage_count INT DEFAULT 0
percentage DECIMAL(5,2)
captured_at DATETIME
```

## 5. API Endpoints

Base path:

```text
/api/v1/coding-profiles
```

### Profile Management

```text
POST   /api/v1/coding-profiles
GET    /api/v1/coding-profiles
GET    /api/v1/coding-profiles/{profileId}
PUT    /api/v1/coding-profiles/{profileId}
DELETE /api/v1/coding-profiles/{profileId}
POST   /api/v1/coding-profiles/{profileId}/verify
POST   /api/v1/coding-profiles/{profileId}/sync
```

### Statistics and Graphs

```text
GET /api/v1/coding-profiles/statistics
GET /api/v1/coding-profiles/{profileId}/statistics
GET /api/v1/coding-profiles/graphs/problems-by-platform
GET /api/v1/coding-profiles/graphs/difficulty-distribution
GET /api/v1/coding-profiles/graphs/rating-trend
GET /api/v1/coding-profiles/graphs/activity
GET /api/v1/coding-profiles/graphs/languages
```

### Ranking and Activity

```text
GET /api/v1/coding-profiles/rankings
GET /api/v1/coding-profiles/activity
GET /api/v1/coding-profiles/dashboard-summary
```

## 6. API Contracts

### Create Coding Profile Request

```json
{
  "platform": "LEETCODE",
  "username": "aarav_codes",
  "profileUrl": "https://leetcode.com/u/aarav_codes",
  "visibility": "PRIVATE"
}
```

### Coding Profile Response

```json
{
  "id": "cp_01J0",
  "platform": "LEETCODE",
  "username": "aarav_codes",
  "profileUrl": "https://leetcode.com/u/aarav_codes",
  "displayName": "Aarav Sharma",
  "verificationStatus": "VERIFIED",
  "visibility": "PRIVATE",
  "lastSyncedAt": "2026-07-17T00:00:00Z",
  "syncStatus": "SUCCESS",
  "statistics": {
    "totalProblemsSolved": 240,
    "easySolved": 90,
    "mediumSolved": 120,
    "hardSolved": 30,
    "ranking": 45892
  }
}
```

### Dashboard Summary Response

```json
{
  "connectedProfiles": 5,
  "verifiedProfiles": 4,
  "totalProblemsSolved": 620,
  "highestRating": 1620,
  "bestRankLabel": "3 Star",
  "githubRepositories": 18,
  "githubContributions": 945,
  "recentActivityCount": 12,
  "problemsByPlatform": [
    { "platform": "LEETCODE", "count": 240 },
    { "platform": "CODEFORCES", "count": 180 }
  ],
  "recentActivity": [
    {
      "platform": "GITHUB",
      "eventType": "REPOSITORY_UPDATED",
      "title": "Updated CareerNest repository",
      "eventDate": "2026-07-17T00:00:00Z"
    }
  ]
}
```

## 7. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/codingprofile/
    controller/
      CodingProfileController.java
      CodingProfileStatsController.java
      CodingProfileActivityController.java
    dto/
      CreateCodingProfileRequest.java
      UpdateCodingProfileRequest.java
      CodingProfileResponse.java
      CodingProfileStatsResponse.java
      CodingProfileDashboardSummaryResponse.java
      CodingActivityResponse.java
      CodingRankingResponse.java
      CodingGraphResponse.java
    entity/
      CodingProfile.java
      CodingProfileStats.java
      CodingActivityEvent.java
      CodingRatingHistory.java
      CodingLanguageStats.java
      CodingPlatform.java
      CodingProfileVerificationStatus.java
    repository/
      CodingProfileRepository.java
      CodingProfileStatsRepository.java
      CodingActivityEventRepository.java
      CodingRatingHistoryRepository.java
      CodingLanguageStatsRepository.java
      CodingProfileQueryRepository.java
    service/
      CodingProfileService.java
      CodingProfileSyncService.java
      CodingProfileStatsService.java
      CodingProfileActivityService.java
      CodingProfileDashboardService.java
    integration/
      GithubCodingClient.java
      LeetCodeCodingClient.java
      CodeChefCodingClient.java
      HackerRankCodingClient.java
      CodeforcesCodingClient.java
    mapper/
      CodingProfileMapper.java
```

## 8. Frontend Folder Structure

```text
frontend/
  src/
    features/
      codingProfiles/
        pages/
          CodingProfilesPage.jsx
          CodingProfileDetailsPage.jsx
        components/
          CodingProfileCard.jsx
          CodingProfileForm.jsx
          CodingPlatformSelector.jsx
          CodingStatsCards.jsx
          ProblemsByPlatformChart.jsx
          DifficultyDistributionChart.jsx
          RatingTrendChart.jsx
          CodingActivityGraph.jsx
          GithubLanguageChart.jsx
          RankingPanel.jsx
          CodingActivityFeed.jsx
          SyncStatusBadge.jsx
          VerificationStatusBadge.jsx
          CodingProfileEmptyState.jsx
          CodingProfileSkeleton.jsx
        services/
          codingProfileService.js
        hooks/
          useCodingProfiles.js
          useCodingProfileStats.js
          useCodingProfileActivity.js
          useCodingProfileSync.js
        validation/
          codingProfileSchemas.js
        utils/
          codingProfileFormatters.js
```

## 9. UI Design

### Coding Profiles Page

Sections:

- Page header with `Add Profile`.
- Connected profile cards.
- Statistics cards.
- Graphs.
- Ranking panel.
- Recent activity feed.

### Profile Card

Each card should show:

- Platform icon/name.
- Username.
- Verification status.
- Last synced time.
- Key stat for platform.
- Sync action.
- Edit/delete actions.

### Graphs

Charts should include:

- Problems solved by platform.
- Difficulty distribution.
- Rating trend.
- GitHub language distribution.
- Activity over time.

### Responsive Strategy

- Desktop: two-column analytics layout.
- Tablet: stacked charts with two-column profile cards.
- Mobile: single-column cards and horizontally compact charts.

## 10. Security Considerations

- All APIs require authentication.
- Backend derives user id from JWT.
- Users can only manage their own coding profiles.
- Platform username and URL must be validated.
- External API errors must not leak internal details.
- OAuth tokens, if added later, must be encrypted.
- Sync endpoints should be rate limited in future.
- Public profile visibility must be explicit.

## 11. Validation Rules

```text
platform: required enum
username: required, 2-120 characters
profileUrl: optional, valid URL
visibility: required enum PRIVATE/PUBLIC
one active profile per user per platform recommended initially
```

Platform URL validation:

```text
GitHub: github.com
LeetCode: leetcode.com
CodeChef: codechef.com
HackerRank: hackerrank.com
Codeforces: codeforces.com
```

## 12. Integration Strategy

### Phase 1

- Manual profile management.
- Store usernames and URLs.
- Manual statistics entry or mock sync-ready structure.
- Dashboard-ready DTOs.

### Phase 2

- Public profile scraping/API integration where permitted.
- GitHub public API integration.
- Codeforces public API integration.
- Platform-specific sync clients.

### Phase 3

- OAuth for GitHub.
- Automated scheduled sync.
- Advanced quality scoring.
- Public portfolio coding profile.

## 13. Analytics Strategy

Backend should calculate:

- Total connected profiles.
- Verified profile count.
- Total problems solved.
- Problems by platform.
- Difficulty distribution.
- Highest rating.
- Ranking snapshot.
- Recent activity count.
- GitHub language distribution.
- Rating trend.

Dashboard integration:

- Coding profile summary card.
- Problems solved chart.
- Recent coding activity.
- Best ranking snapshot.

## 14. Future Scalability

This module should support:

- Scheduled platform sync jobs.
- Redis cache for profile stats.
- GitHub OAuth.
- LeetCode contest history.
- Codeforces contest history.
- Public coding portfolio.
- Resume builder coding stats import.
- Learning tracker integration for DSA progress.
- AI recommendations based on weak problem categories.

## 15. Industry Best Practices

- Separate profile identity from stats snapshots.
- Store raw platform-specific fields in JSON where schemas differ.
- Normalize only dashboard-critical metrics.
- Make sync clients platform-specific.
- Handle partial sync failure gracefully.
- Use backend aggregation for charts.
- Use pagination for activity feeds.
- Keep verification separate from sync.
- Respect platform rate limits and terms.

## 16. Testing Strategy

### Backend Tests

- User can add valid coding profile.
- Invalid platform URL is rejected.
- User cannot access another user's coding profile.
- Duplicate platform profile is handled correctly.
- Sync failure stores sync error safely.
- Dashboard summary aggregates stats correctly.
- Ranking endpoint returns platform-specific values.
- Activity feed pagination works.

### Frontend Tests

- Profile cards render connected platforms.
- Add profile form validates platform and username.
- Invalid URL shows field error.
- Sync status updates UI.
- Charts handle empty data.
- Ranking panel renders platform-specific ranking.
- Mobile layout remains readable.

## 17. Swagger Documentation

Swagger group:

```text
Coding Profiles
```

Document:

- Profile CRUD.
- Verify profile.
- Sync profile.
- Statistics.
- Graph endpoints.
- Rankings.
- Activity.
- Dashboard summary.

Each endpoint must include:

- Authentication requirement.
- Request schema.
- Response schema.
- Validation examples.
- Ownership error examples.
- Sync error examples.

## 18. Implementation Order After Approval

1. Create coding profile entities and enums.
2. Create stats, activity, rating history, and language stats entities.
3. Create repositories and query repository.
4. Define DTOs and validation.
5. Build mapper.
6. Implement profile CRUD service.
7. Implement verification placeholder service.
8. Implement sync service abstraction.
9. Implement stats and dashboard summary service.
10. Implement activity and graph endpoints.
11. Add controllers and Swagger docs.
12. Add backend tests.
13. Build frontend profile cards and form.
14. Build statistics cards.
15. Build Recharts graphs.
16. Build ranking panel and activity feed.
17. Integrate dashboard widgets.
18. Complete responsive polish.

## 19. Approval Gate

No Coding Profiles implementation should begin until this architecture is approved.

After approval, implementation should begin with backend coding profile entities, secure CRUD APIs, statistics snapshots, and dashboard summary endpoints.
