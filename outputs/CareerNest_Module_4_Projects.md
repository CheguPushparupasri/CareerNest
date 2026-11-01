# CareerNest Module 4: Projects Portfolio

## 1. Purpose of the Module

The Projects module allows students and freshers to build a professional portfolio inside CareerNest. Users can create, edit, delete, search, filter, and showcase projects with images, screenshots, GitHub links, live demo links, tech stack, tags, and analytics.

This module supports career readiness by helping users maintain project evidence that can later be reused in resumes, dashboards, profiles, and job applications.

## 2. Functional Requirements

### Create Projects

Users can create projects with:

- Title.
- Short summary.
- Detailed description.
- Problem statement.
- Solution overview.
- Tech stack.
- Tags.
- GitHub repository link.
- Live demo link.
- Project status.
- Start date and end date.
- Featured flag.
- Cover image.
- Screenshots.

### Edit Projects

Users can update all project fields except ownership. The system must preserve audit metadata and validate all updated values.

### Delete Projects

Users can delete their own projects.

Recommended behavior:

- Soft delete by default.
- Preserve analytics and audit history.
- Exclude deleted projects from normal lists.

### Upload Images

Users can upload:

- One cover image.
- Multiple screenshots.

Image handling:

- Upload through backend.
- Store in Cloudinary.
- Validate file size, MIME type, and extension.
- Store Cloudinary public id for deletion/replacement.
- Allow screenshot ordering.

### GitHub Link

Users can attach a repository URL.

Validation:

- Must be a valid URL.
- Prefer `github.com` domain for GitHub field.
- Allow empty value for private/unpublished projects.

### Live Demo

Users can attach a deployed project URL.

Validation:

- Must be a valid URL.
- Support common deployment hosts like Vercel, Netlify, Render, Railway, GitHub Pages, or custom domains.

### Tech Stack

Users can define technologies used in the project:

- React.
- Java.
- Spring Boot.
- MySQL.
- Tailwind CSS.
- Docker.
- Any custom technology.

Tech stack should be normalized enough for search and analytics.

### Screenshots

Users can upload multiple screenshots showing:

- Home screen.
- Dashboard.
- Key workflows.
- Mobile responsive view.
- Admin panel, if applicable.

Each screenshot can have:

- Image URL.
- Caption.
- Display order.

### Tags

Tags help categorize projects:

- Full Stack.
- Frontend.
- Backend.
- SaaS.
- AI.
- Data Visualization.
- E-commerce.
- Portfolio.
- Academic.

Tags should support filtering.

### Project Analytics

Initial analytics should include:

- Total projects.
- Featured projects.
- Projects by status.
- Most used technologies.
- Recent project activity.
- Projects with GitHub links.
- Projects with live demos.
- Screenshot count.

Future analytics may include:

- Portfolio view count.
- Recruiter clicks.
- GitHub click-throughs.
- Live demo click-throughs.

### Search

Users can search by:

- Title.
- Summary.
- Description.
- Tech stack.
- Tags.

### Filters

Filter projects by:

- Status.
- Featured.
- Tech stack.
- Tags.
- Date range.
- Has GitHub link.
- Has live demo link.

### Pagination

All list APIs must support pagination:

- Page number.
- Page size.
- Sort field.
- Sort direction.

Default sorting:

```text
updated_at DESC
```

### Validation

Validation must exist on both frontend and backend:

- Title required.
- Title max length.
- Summary max length.
- Description max length.
- URL validation.
- Date consistency.
- File type validation.
- File size validation.
- Max screenshot count.
- Tech stack and tag limits.

## 3. Non-Functional Requirements

- User-owned data isolation.
- Fast project listing with pagination.
- Search and filter queries must be index-friendly.
- Image uploads must be secure and size-limited.
- Soft delete should avoid accidental permanent loss.
- Reusable UI cards for project listing.
- Responsive portfolio management UI.
- API responses must use standard CareerNest response envelope.
- Project analytics should be calculated without loading all raw project records into frontend.
- Future-ready for public portfolio sharing.

## 4. Database Tables

### `projects`

```text
id BIGINT PK
user_id BIGINT FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
title VARCHAR(140) NOT NULL
slug VARCHAR(180)
summary VARCHAR(280)
description TEXT
problem_statement TEXT
solution_overview TEXT
github_url VARCHAR(500)
live_demo_url VARCHAR(500)
status VARCHAR(40) NOT NULL
featured BOOLEAN DEFAULT FALSE
cover_image_url VARCHAR(500)
cover_image_public_id VARCHAR(255)
start_date DATE
end_date DATE
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

Recommended project statuses:

```text
PLANNED
IN_PROGRESS
COMPLETED
ARCHIVED
```

Indexes:

```text
idx_projects_user_id
idx_projects_user_status
idx_projects_user_featured
idx_projects_updated_at
uk_projects_public_id
```

### `project_technologies`

```text
id BIGINT PK
project_id BIGINT FK projects(id)
name VARCHAR(80) NOT NULL
category VARCHAR(80)
display_order INT DEFAULT 0
created_at DATETIME
```

Indexes:

```text
idx_project_technologies_project_id
idx_project_technologies_name
```

### `project_tags`

```text
id BIGINT PK
project_id BIGINT FK projects(id)
name VARCHAR(60) NOT NULL
created_at DATETIME
```

Indexes:

```text
idx_project_tags_project_id
idx_project_tags_name
```

### `project_screenshots`

```text
id BIGINT PK
project_id BIGINT FK projects(id)
image_url VARCHAR(500) NOT NULL
image_public_id VARCHAR(255) NOT NULL
caption VARCHAR(160)
display_order INT DEFAULT 0
created_at DATETIME
updated_at DATETIME
```

Indexes:

```text
idx_project_screenshots_project_id
```

### Future Table: `project_analytics_events`

For public portfolio analytics later.

```text
id BIGINT PK
project_id BIGINT FK projects(id)
user_id BIGINT FK users(id)
event_type VARCHAR(60) NOT NULL
metadata JSON
created_at DATETIME
```

## 5. API Endpoints

Base path:

```text
/api/v1/projects
```

### Project CRUD

```text
POST   /api/v1/projects
GET    /api/v1/projects
GET    /api/v1/projects/{projectId}
PUT    /api/v1/projects/{projectId}
PATCH  /api/v1/projects/{projectId}/featured
DELETE /api/v1/projects/{projectId}
```

### Project Media

```text
POST   /api/v1/projects/{projectId}/cover-image
DELETE /api/v1/projects/{projectId}/cover-image
POST   /api/v1/projects/{projectId}/screenshots
PUT    /api/v1/projects/{projectId}/screenshots/reorder
PATCH  /api/v1/projects/{projectId}/screenshots/{screenshotId}
DELETE /api/v1/projects/{projectId}/screenshots/{screenshotId}
```

### Search, Filters, Analytics

```text
GET /api/v1/projects/search
GET /api/v1/projects/analytics
GET /api/v1/projects/filters
```

### Query Parameters

```text
GET /api/v1/projects?page=0&size=12&sort=updatedAt,desc
GET /api/v1/projects/search?q=react&status=COMPLETED&tag=SaaS&technology=Spring%20Boot
GET /api/v1/projects/search?featured=true&hasGithub=true&hasLiveDemo=true
```

## 6. API Contracts

### Create Project Request

```json
{
  "title": "CareerNest",
  "summary": "A SaaS platform for students to manage their career journey.",
  "description": "CareerNest helps students manage resumes, applications, skills, and interviews.",
  "problemStatement": "Students struggle to organize career preparation in one place.",
  "solutionOverview": "A modular SaaS workspace for profile, resume, jobs, skills, and analytics.",
  "githubUrl": "https://github.com/example/careernest",
  "liveDemoUrl": "https://careernest.example.com",
  "status": "IN_PROGRESS",
  "featured": true,
  "startDate": "2026-07-01",
  "endDate": null,
  "technologies": [
    { "name": "React", "category": "Frontend" },
    { "name": "Spring Boot", "category": "Backend" },
    { "name": "MySQL", "category": "Database" }
  ],
  "tags": ["Full Stack", "SaaS", "Career"]
}
```

### Project Response

```json
{
  "id": "prj_01J0",
  "title": "CareerNest",
  "slug": "careernest",
  "summary": "A SaaS platform for students to manage their career journey.",
  "description": "CareerNest helps students manage resumes, applications, skills, and interviews.",
  "githubUrl": "https://github.com/example/careernest",
  "liveDemoUrl": "https://careernest.example.com",
  "status": "IN_PROGRESS",
  "featured": true,
  "coverImageUrl": "https://res.cloudinary.com/demo/image/upload/project-cover.png",
  "startDate": "2026-07-01",
  "endDate": null,
  "technologies": [
    { "name": "React", "category": "Frontend" }
  ],
  "tags": ["Full Stack", "SaaS"],
  "screenshots": [],
  "createdAt": "2026-07-17T00:00:00Z",
  "updatedAt": "2026-07-17T00:00:00Z"
}
```

### Analytics Response

```json
{
  "totalProjects": 8,
  "featuredProjects": 3,
  "completedProjects": 5,
  "projectsWithGithub": 7,
  "projectsWithLiveDemo": 4,
  "totalScreenshots": 18,
  "projectsByStatus": [
    { "status": "COMPLETED", "count": 5 },
    { "status": "IN_PROGRESS", "count": 2 },
    { "status": "PLANNED", "count": 1 }
  ],
  "topTechnologies": [
    { "name": "React", "count": 6 },
    { "name": "Spring Boot", "count": 4 }
  ]
}
```

## 7. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/project/
    controller/
      ProjectController.java
      ProjectMediaController.java
      ProjectAnalyticsController.java
    dto/
      CreateProjectRequest.java
      UpdateProjectRequest.java
      ProjectResponse.java
      ProjectListItemResponse.java
      ProjectTechnologyRequest.java
      ProjectTechnologyResponse.java
      ProjectScreenshotResponse.java
      ReorderProjectScreenshotsRequest.java
      ProjectAnalyticsResponse.java
      ProjectSearchCriteria.java
    entity/
      Project.java
      ProjectTechnology.java
      ProjectTag.java
      ProjectScreenshot.java
      ProjectStatus.java
    repository/
      ProjectRepository.java
      ProjectTechnologyRepository.java
      ProjectTagRepository.java
      ProjectScreenshotRepository.java
      ProjectQueryRepository.java
    service/
      ProjectService.java
      ProjectServiceImpl.java
      ProjectMediaService.java
      ProjectAnalyticsService.java
    mapper/
      ProjectMapper.java
```

### Backend Responsibilities

- Enforce project ownership.
- Validate create/update requests.
- Handle soft deletion.
- Upload and delete Cloudinary images.
- Store normalized technologies and tags.
- Return paginated project lists.
- Support search and filtering.
- Provide analytics DTOs.

## 8. Frontend Folder Structure

```text
frontend/
  src/
    features/
      projects/
        pages/
          ProjectsPage.jsx
          ProjectDetailsPage.jsx
          CreateProjectPage.jsx
          EditProjectPage.jsx
        components/
          ProjectCard.jsx
          ProjectForm.jsx
          ProjectFilters.jsx
          ProjectSearchBar.jsx
          ProjectAnalyticsCards.jsx
          ProjectTechStackInput.jsx
          ProjectTagInput.jsx
          ProjectImageUploader.jsx
          ProjectScreenshotManager.jsx
          ProjectStatusBadge.jsx
          ProjectDeleteDialog.jsx
          ProjectEmptyState.jsx
          ProjectSkeleton.jsx
        services/
          projectService.js
        hooks/
          useProjects.js
          useProjectDetails.js
          useProjectAnalytics.js
          useProjectMutations.js
        validation/
          projectSchemas.js
        utils/
          projectFormatters.js
```

## 9. UI Design

### Projects Page

The Projects page should be a portfolio management workspace.

Primary sections:

- Page header with `Create Project` action.
- Analytics cards.
- Search and filters.
- Project grid/list.
- Pagination controls.

### Project Card

Each card should show:

- Cover image or fallback.
- Title.
- Summary.
- Status badge.
- Featured marker.
- Tech stack chips.
- Tags.
- GitHub and live demo icon buttons.
- Updated date.
- Edit/delete actions.

### Create/Edit Form

Use grouped sections:

- Basic details.
- Description.
- Links.
- Tech stack.
- Tags.
- Timeline.
- Media.

The form must include:

- Field-level errors.
- Submit loading state.
- Unsaved changes protection in future.

### Project Details Page

Show:

- Cover image.
- Title and status.
- Summary and description.
- Problem and solution sections.
- Tech stack.
- Tags.
- Screenshots gallery.
- GitHub and live demo actions.
- Edit button.

### Responsive Strategy

- Desktop: analytics row, filter bar, 3-column project grid.
- Tablet: 2-column project grid.
- Mobile: single-column cards, collapsible filters, full-width form controls.

## 10. Security Considerations

- All project APIs require authentication.
- Backend must derive user id from JWT principal.
- Users can only access and mutate their own projects.
- Project ID lookup must include owner constraint.
- Validate uploaded file type, size, and count.
- Do not trust client-provided Cloudinary URLs.
- Delete/replacement must remove old Cloudinary assets where safe.
- Prevent XSS by sanitizing or safely rendering rich descriptions.
- Admin access to projects, if added later, must be explicit and audited.

## 11. Validation Rules

### Project Fields

```text
title: required, 3-140 characters
summary: optional, max 280 characters
description: optional, max 5000 characters
problemStatement: optional, max 2000 characters
solutionOverview: optional, max 2000 characters
githubUrl: optional, valid URL, preferably github.com
liveDemoUrl: optional, valid URL
status: required enum
startDate: optional
endDate: optional, must be after startDate
technologies: max 20
technology name: 1-80 characters
tags: max 10
tag name: 1-60 characters
```

### Image Fields

```text
cover image max size: 3 MB
screenshot max size: 5 MB each
max screenshots per project: 8
allowed types: image/jpeg, image/png, image/webp
```

## 12. Project Analytics

### Backend Calculations

- Count all non-deleted projects.
- Count featured projects.
- Count completed projects.
- Count projects by status.
- Count projects with GitHub URL.
- Count projects with live demo URL.
- Count total screenshots.
- Count technologies grouped by name.

### Dashboard Integration

Dashboard should show:

- Recent projects.
- Total project count.
- Featured project count.
- Project timeline events.

When a project is created or completed, a timeline event should be added.

## 13. Search and Filtering Strategy

### Simple Phase

Use Spring Data JPA specifications or custom query repository:

- Search with `LIKE` on title, summary, description.
- Join tags and technologies for filtering.
- Use pagination.

### Future Phase

For larger scale:

- Add full-text index.
- Use Elasticsearch/OpenSearch.
- Add public portfolio search.

## 14. Future Scalability

The Projects module should support:

- Public portfolio pages.
- Recruiter-facing project view.
- Project import from GitHub.
- GitHub repository metadata sync.
- Project analytics events.
- AI project description improvement.
- Resume section reuse.
- Team/collaborator projects.
- Project attachments.
- Video demos.

## 15. Industry Best Practices

- Keep project ownership enforcement in service/repository layer.
- Use DTOs and mappers.
- Do not expose JPA entities.
- Use pagination for all list endpoints.
- Use soft delete.
- Use backend-controlled media upload.
- Keep analytics as aggregated DTOs.
- Use transaction boundaries for project create/update with nested technologies/tags.
- Avoid storing duplicate image binaries in database.
- Add audit/timeline events for create, update, delete, and status changes.

## 16. Testing Strategy

### Backend Tests

- Create project succeeds with valid data.
- Create project fails with invalid title.
- Duplicate ownership cannot be bypassed.
- User cannot access another user's project.
- Update project replaces technologies and tags correctly.
- Delete project performs soft delete.
- Search returns matching title/tag/technology.
- Filters return correct status/featured results.
- Pagination metadata is correct.
- Analytics counts are correct.
- Invalid image upload is rejected.

### Frontend Tests

- Project list renders loading state.
- Empty state renders when no projects exist.
- Project form validates required fields.
- URL fields show validation errors.
- Filters update query state.
- Pagination changes page.
- Delete dialog requires confirmation.
- Image upload shows preview and errors.

## 17. Swagger Documentation

Swagger group:

```text
Projects
```

Document:

- CRUD endpoints.
- Search/filter parameters.
- Pagination response.
- Multipart image upload endpoints.
- Analytics response.
- Validation error examples.
- Authentication requirement.

## 18. Implementation Order After Approval

1. Define project entities and enums.
2. Create repositories and query repository.
3. Create DTOs and validation annotations.
4. Build mapper.
5. Implement project service.
6. Implement media upload service with Cloudinary.
7. Implement analytics service.
8. Implement controllers.
9. Add Swagger annotations.
10. Add backend tests.
11. Build frontend reusable project cards.
12. Build project form and validation.
13. Build image uploader and screenshot manager.
14. Build search, filters, and pagination.
15. Build project analytics cards.
16. Add responsive polish.

## 19. Approval Gate

No Projects module implementation should begin until this architecture is approved.

After approval, implementation should begin with backend project entities, DTOs, repositories, and ownership-secure CRUD APIs.
