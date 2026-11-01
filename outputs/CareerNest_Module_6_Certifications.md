# CareerNest Module 6: Certifications

## 1. Purpose of the Module

The Certifications module allows users to upload, organize, verify, search, and track professional certifications. It helps students and freshers maintain credible proof of learning and achievements that can be reused across the profile, dashboard, resume builder, and job applications.

This module must support secure Cloudinary uploads, structured metadata, expiry reminders, verification status, analytics, and responsive portfolio-style UI.

## 2. Functional Requirements

### Upload Certificates

Users can upload certificate files with:

- Certificate title.
- Issuer.
- Category.
- Credential ID.
- Credential URL.
- Issue date.
- Expiry date.
- Does not expire flag.
- Description.
- Skills covered.
- Certificate file.
- Thumbnail or preview.

Supported file types:

- PDF.
- JPEG.
- PNG.
- WebP.

### Cloudinary Integration

Certificate files must be uploaded through backend-controlled Cloudinary integration.

Cloudinary responsibilities:

- Store certificate files.
- Store secure URLs.
- Store public IDs.
- Delete or replace old files when needed.
- Generate preview thumbnails where possible.

Backend must not trust client-provided Cloudinary URLs.

### Categories

Users can categorize certifications:

- Programming.
- Web Development.
- Backend.
- Frontend.
- Cloud.
- DevOps.
- Database.
- Data Science.
- AI/ML.
- Cybersecurity.
- Soft Skills.
- Academic.
- Other.

Categories should support filtering and analytics.

### Verification

Certification verification should support:

- User-provided credential URL.
- Credential ID.
- Verification status.
- Admin/manual verification in future.

Recommended verification statuses:

```text
UNVERIFIED
PENDING_REVIEW
VERIFIED
REJECTED
EXPIRED
```

Initial implementation can mark certifications as `UNVERIFIED` by default unless a credential URL is provided. Full third-party verification should be a future enhancement.

### Expiry

Track certificate validity:

- Issue date.
- Expiry date.
- Does not expire.
- Expiring soon indicator.
- Expired indicator.
- Dashboard reminders.

Expiry rules:

- Expiry date must be after issue date.
- If `doesNotExpire` is true, expiry date must be null.
- Expiring soon default window: 30 days.

### Search

Users can search certifications by:

- Title.
- Issuer.
- Category.
- Skills.
- Credential ID.

### Filters

Users can filter by:

- Category.
- Verification status.
- Issuer.
- Expired.
- Expiring soon.
- Does not expire.
- Date range.
- Skills.

### Analytics

Certification analytics should show:

- Total certifications.
- Verified certifications.
- Expired certifications.
- Expiring soon.
- Certifications by category.
- Certifications by issuer.
- Recently added certifications.
- Skills covered by certifications.

### Dashboard

Dashboard integration should include:

- Recent certifications.
- Expiring soon certifications.
- Total certification count.
- Verified certification count.
- Career timeline events for new certificates.

### Responsive UI

Support:

- Desktop grid/list views.
- Tablet two-column cards.
- Mobile single-column cards.
- Mobile-friendly upload flow.
- Responsive filters.

## 3. Non-Functional Requirements

- All certificate records must be user-scoped.
- File uploads must be validated and size-limited.
- Cloudinary secrets must stay backend-only.
- List endpoints must be paginated.
- Search/filter queries must be efficient.
- UI must handle loading, empty, error, and upload progress states.
- Certificate files should not be permanently deleted accidentally without confirmation.
- Module must integrate cleanly with resume builder and dashboard.
- Audit/timeline events should be created for certificate upload, update, verification, and expiry status changes.

## 4. Database Tables

### `certification_categories`

Stores managed categories.

```text
id BIGINT PK
name VARCHAR(100) UNIQUE NOT NULL
description VARCHAR(255)
is_active BOOLEAN DEFAULT TRUE
created_at DATETIME
updated_at DATETIME
```

### `certifications`

Stores user certification metadata.

```text
id BIGINT PK
user_id BIGINT FK users(id)
category_id BIGINT FK certification_categories(id)
public_id VARCHAR(64) UNIQUE NOT NULL
title VARCHAR(160) NOT NULL
issuer VARCHAR(160) NOT NULL
credential_id VARCHAR(160)
credential_url VARCHAR(500)
description TEXT
issue_date DATE
expiry_date DATE
does_not_expire BOOLEAN DEFAULT FALSE
verification_status VARCHAR(40) NOT NULL
file_url VARCHAR(500)
file_public_id VARCHAR(255)
file_resource_type VARCHAR(40)
thumbnail_url VARCHAR(500)
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

Indexes:

```text
idx_certifications_user_id
idx_certifications_user_category
idx_certifications_user_status
idx_certifications_expiry_date
uk_certifications_public_id
```

### `certification_skills`

Stores skills represented by a certification.

```text
id BIGINT PK
certification_id BIGINT FK certifications(id)
skill_name VARCHAR(100) NOT NULL
created_at DATETIME
```

Indexes:

```text
idx_certification_skills_certification_id
idx_certification_skills_skill_name
```

### Future Table: `certification_verification_events`

For manual/admin or third-party verification workflows.

```text
id BIGINT PK
certification_id BIGINT FK certifications(id)
actor_user_id BIGINT NULL FK users(id)
from_status VARCHAR(40)
to_status VARCHAR(40)
note VARCHAR(500)
created_at DATETIME
```

## 5. API Endpoints

Base path:

```text
/api/v1/certifications
```

### Certification CRUD

```text
POST   /api/v1/certifications
GET    /api/v1/certifications
GET    /api/v1/certifications/{certificationId}
PUT    /api/v1/certifications/{certificationId}
DELETE /api/v1/certifications/{certificationId}
```

### Certificate File Upload

```text
POST   /api/v1/certifications/{certificationId}/file
DELETE /api/v1/certifications/{certificationId}/file
```

### Categories

```text
GET /api/v1/certification-categories
```

Admin future endpoints:

```text
POST   /api/v1/admin/certification-categories
PUT    /api/v1/admin/certification-categories/{categoryId}
DELETE /api/v1/admin/certification-categories/{categoryId}
```

### Search, Filters, Analytics

```text
GET /api/v1/certifications/search
GET /api/v1/certifications/filters
GET /api/v1/certifications/analytics
GET /api/v1/certifications/expiring-soon
```

### Verification

```text
PATCH /api/v1/certifications/{certificationId}/verification-status
```

For initial implementation, verification status updates should be restricted to admin or internal service logic.

## 6. Query Parameters

```text
GET /api/v1/certifications?page=0&size=12&sort=issueDate,desc
GET /api/v1/certifications/search?q=aws&category=Cloud&status=VERIFIED
GET /api/v1/certifications/search?expired=false&expiringSoon=true
GET /api/v1/certifications/search?issuer=Coursera&skill=React
```

## 7. API Contracts

### Create Certification Request

```json
{
  "title": "AWS Cloud Practitioner",
  "issuer": "Amazon Web Services",
  "categoryId": 3,
  "credentialId": "AWS-123456",
  "credentialUrl": "https://www.credly.com/badges/example",
  "description": "Foundation-level AWS cloud certification.",
  "issueDate": "2026-06-01",
  "expiryDate": "2029-06-01",
  "doesNotExpire": false,
  "skills": ["Cloud Computing", "AWS", "IAM", "EC2"]
}
```

### Certification Response

```json
{
  "id": "cert_01J0",
  "title": "AWS Cloud Practitioner",
  "issuer": "Amazon Web Services",
  "category": {
    "id": 3,
    "name": "Cloud"
  },
  "credentialId": "AWS-123456",
  "credentialUrl": "https://www.credly.com/badges/example",
  "description": "Foundation-level AWS cloud certification.",
  "issueDate": "2026-06-01",
  "expiryDate": "2029-06-01",
  "doesNotExpire": false,
  "verificationStatus": "UNVERIFIED",
  "fileUrl": "https://res.cloudinary.com/demo/raw/upload/certificate.pdf",
  "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/certificate-preview.png",
  "skills": ["Cloud Computing", "AWS", "IAM", "EC2"],
  "expired": false,
  "expiringSoon": false,
  "createdAt": "2026-07-17T00:00:00Z",
  "updatedAt": "2026-07-17T00:00:00Z"
}
```

### Analytics Response

```json
{
  "totalCertifications": 12,
  "verifiedCertifications": 5,
  "expiredCertifications": 1,
  "expiringSoon": 2,
  "certificationsByCategory": [
    { "category": "Cloud", "count": 4 },
    { "category": "Frontend", "count": 3 }
  ],
  "certificationsByIssuer": [
    { "issuer": "Coursera", "count": 4 },
    { "issuer": "Amazon Web Services", "count": 2 }
  ],
  "topSkills": [
    { "skill": "React", "count": 3 },
    { "skill": "AWS", "count": 2 }
  ]
}
```

## 8. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/certification/
    controller/
      CertificationController.java
      CertificationFileController.java
      CertificationCategoryController.java
      CertificationAnalyticsController.java
    dto/
      CreateCertificationRequest.java
      UpdateCertificationRequest.java
      CertificationResponse.java
      CertificationListItemResponse.java
      CertificationCategoryResponse.java
      CertificationSearchCriteria.java
      CertificationAnalyticsResponse.java
      UpdateCertificationVerificationRequest.java
    entity/
      Certification.java
      CertificationCategory.java
      CertificationSkill.java
      CertificationVerificationStatus.java
    repository/
      CertificationRepository.java
      CertificationCategoryRepository.java
      CertificationSkillRepository.java
      CertificationQueryRepository.java
    service/
      CertificationService.java
      CertificationServiceImpl.java
      CertificationFileService.java
      CertificationAnalyticsService.java
      CertificationCategoryService.java
    mapper/
      CertificationMapper.java
```

## 9. Frontend Folder Structure

```text
frontend/
  src/
    features/
      certifications/
        pages/
          CertificationsPage.jsx
          CertificationDetailsPage.jsx
          CreateCertificationPage.jsx
          EditCertificationPage.jsx
        components/
          CertificationCard.jsx
          CertificationForm.jsx
          CertificationFileUploader.jsx
          CertificationFilters.jsx
          CertificationSearchBar.jsx
          CertificationAnalyticsCards.jsx
          CertificationStatusBadge.jsx
          CertificationExpiryBadge.jsx
          CertificationDeleteDialog.jsx
          CertificationEmptyState.jsx
          CertificationSkeleton.jsx
        services/
          certificationService.js
        hooks/
          useCertifications.js
          useCertificationDetails.js
          useCertificationAnalytics.js
          useCertificationMutations.js
        validation/
          certificationSchemas.js
        utils/
          certificationFormatters.js
```

## 10. UI Design

### Certifications Page

Primary layout:

- Page header with `Upload Certificate` action.
- Analytics cards.
- Search bar.
- Filter controls.
- Certification grid/list.
- Pagination.

### Certification Card

Each card should show:

- Certificate thumbnail or file type icon.
- Title.
- Issuer.
- Category badge.
- Verification badge.
- Expiry badge.
- Issue date.
- Skills chips.
- Credential link.
- View/edit/delete actions.

### Upload/Edit Form

Grouped sections:

- Certificate details.
- Dates and expiry.
- Verification information.
- Skills.
- File upload.

The form must include:

- Field-level validation.
- Upload progress.
- File preview.
- Clear success/error messages.

### Details Page

Show:

- Certificate preview.
- Metadata.
- Skills.
- Verification status.
- Expiry state.
- Credential URL.
- Edit and delete actions.

### Responsive Strategy

- Desktop: analytics row, filter bar, 3-column card grid.
- Tablet: 2-column card grid.
- Mobile: single-column cards, collapsible filters, stacked form sections.

## 11. Security Considerations

- All certification APIs require authentication.
- Backend derives user id from JWT.
- Users can only access their own certifications.
- Certificate lookup must include owner constraint.
- File uploads must validate MIME type, extension, and size.
- Cloudinary upload must happen through backend service.
- Cloudinary API secrets must never reach frontend.
- Credential URLs should be validated to prevent unsafe protocols.
- Soft delete should be used by default.
- Admin verification changes must be audited.

## 12. Validation Rules

### Certification Fields

```text
title: required, 3-160 characters
issuer: required, 2-160 characters
categoryId: required, must exist
credentialId: optional, max 160 characters
credentialUrl: optional, valid URL
description: optional, max 2000 characters
issueDate: optional, cannot be in the future unless allowed explicitly
expiryDate: optional, must be after issueDate
doesNotExpire: boolean
skills: max 20
skill name: 1-100 characters
```

### File Fields

```text
max file size: 10 MB
allowed MIME types:
  application/pdf
  image/jpeg
  image/png
  image/webp
```

## 13. Analytics Strategy

### Backend Calculations

- Count non-deleted certifications.
- Count verified certifications.
- Count expired certifications.
- Count expiring soon certifications.
- Group certifications by category.
- Group certifications by issuer.
- Group skills from certification skills.
- Fetch recently added certifications.

### Dashboard Integration

Dashboard widgets should use:

- Recent certifications.
- Expiring soon certifications.
- Total certification count.
- Verified count.

Timeline events:

- `CERTIFICATION_ADDED`
- `CERTIFICATION_UPDATED`
- `CERTIFICATION_VERIFIED`
- `CERTIFICATION_EXPIRED`

## 14. Search and Filtering Strategy

### Initial Phase

Use Spring Data JPA specification or custom query repository:

- Search title, issuer, credential ID.
- Join category and skills.
- Filter by status and expiry.
- Paginate all list results.

### Future Phase

- Full-text search.
- Public portfolio certificate search.
- Verification provider integrations.

## 15. Future Scalability

The Certifications module should support:

- Credly verification integration.
- Coursera/Udemy/LinkedIn import.
- Certificate OCR metadata extraction.
- Public portfolio certificate display.
- Recruiter certificate verification view.
- Renewal reminders through notifications.
- Admin verification workflow.
- AI skill extraction from certificate PDFs.
- Resume builder smart import.

## 16. Industry Best Practices

- Store metadata separately from files.
- Do not store certificate binaries in MySQL.
- Store Cloudinary public IDs for lifecycle management.
- Use soft delete.
- Use DTOs and mappers.
- Keep upload handling in a dedicated service.
- Return paginated responses for lists.
- Enforce ownership at backend service/repository layer.
- Keep verification extensible.
- Make expiry calculations backend-authoritative.

## 17. Testing Strategy

### Backend Tests

- Create certification succeeds with valid data.
- Invalid expiry date is rejected.
- User cannot access another user's certificate.
- Search returns matching title, issuer, and skill.
- Filters return correct category/status/expiry data.
- Pagination metadata is correct.
- Analytics counts are correct.
- File upload rejects unsupported MIME type.
- File upload rejects oversized file.
- Soft delete excludes certification from lists.

### Frontend Tests

- Certification list loading state renders.
- Empty state renders when no certifications exist.
- Upload form validates required fields.
- Expiry rules show field errors.
- File uploader validates type and size.
- Filters update list query.
- Pagination changes page.
- Analytics cards render correctly.
- Mobile card layout remains readable.

## 18. Swagger Documentation

Swagger group:

```text
Certifications
```

Document:

- Certification CRUD.
- Multipart file upload.
- Category endpoint.
- Search and filters.
- Analytics.
- Expiring soon endpoint.
- Verification status endpoint.

Each endpoint must include:

- Authentication requirement.
- Request schema.
- Response schema.
- Validation error examples.
- Ownership error examples.
- Multipart upload details.

## 19. Implementation Order After Approval

1. Create certification category, certification, and skill entities.
2. Seed certification categories.
3. Create repositories and query repository.
4. Define DTOs and validation rules.
5. Build certification mapper.
6. Implement certification CRUD service.
7. Implement Cloudinary file upload service.
8. Implement search/filter service.
9. Implement analytics service.
10. Add controllers and Swagger docs.
11. Add backend tests.
12. Build frontend certification cards.
13. Build upload/edit form.
14. Build file uploader and preview.
15. Build search, filters, pagination.
16. Build analytics cards.
17. Integrate dashboard recent/expiry widgets.
18. Complete responsive polish.

## 20. Approval Gate

No Certifications module implementation should begin until this architecture is approved.

After approval, implementation should begin with backend certification entities, category seeding, secure CRUD APIs, and Cloudinary-backed file upload.
