# CareerNest Module 5: ATS Resume Builder

## 1. Purpose of the Module

The ATS Resume Builder helps students and freshers create professional, applicant-tracking-system-friendly resumes using structured CareerNest profile data. Users can create multiple resume versions, choose templates, drag and drop sections, preview changes live, calculate ATS score, export PDFs, and maintain version history.

This module must behave like a serious SaaS resume product, not a static form generator.

## 2. Functional Requirements

### Multiple Templates

Users can choose from multiple ATS-friendly templates:

- Classic.
- Modern.
- Compact.
- Technical.
- Fresher.
- Internship.

Template rules:

- All templates must be readable by ATS parsers.
- Avoid heavy graphics, tables, icons inside content, and complex columns in ATS mode.
- Maintain consistent semantic section headings.
- Allow visual variety without breaking parsability.

### Drag and Drop Sections

Users can reorder resume sections:

- Summary.
- Skills.
- Experience.
- Education.
- Projects.
- Certifications.
- Achievements.
- Links.

Rules:

- Required sections cannot be deleted, only hidden if product allows.
- Display order must persist.
- Drag and drop must work on desktop.
- Mobile should support move up/down controls as a fallback.

### Live Preview

Resume preview updates as the user edits:

- Template changes.
- Section ordering.
- Field content.
- Visibility toggles.
- Skills, projects, certifications, and achievements.

Preview modes:

- Desktop editor split view.
- Mobile preview tab.
- Print/PDF preview.

### PDF Download

Users can export resumes as PDFs.

PDF rules:

- Export must match preview closely.
- PDF should be selectable text, not a screenshot.
- File name should be clean, for example `Aarav_Sharma_Full_Stack_Resume.pdf`.
- Store generated PDF in Cloudinary or return direct download depending on implementation phase.

### Version History

Users can maintain versions:

- Automatic version on major save.
- Manual duplicate version.
- Restore previous version.
- Rename version.
- Mark version as primary.

Version metadata:

- Version number.
- Template.
- Resume title.
- Created date.
- Last updated date.
- ATS score snapshot.

### ATS Score

Calculate ATS score based on resume quality signals:

- Contact information completeness.
- Clear summary.
- Skills coverage.
- Experience/project bullet strength.
- Quantified achievements.
- Education completeness.
- Certification relevance.
- Keyword match against target role or job description.
- Section readability.
- Length and formatting.
- Missing critical fields.

ATS score should include:

- Overall score.
- Category scores.
- Actionable recommendations.
- Severity: high, medium, low.

### Skills

Users can add skills from:

- Existing CareerNest skill profile.
- Resume-specific custom skills.
- Suggested skills by target role.

Skills should support:

- Category.
- Proficiency, optional.
- Display order.
- Visibility per resume.

### Experience

Users can add experience:

- Company.
- Role.
- Employment type.
- Location.
- Start/end dates.
- Currently working flag.
- Bullet points.

Bullet points should encourage:

- Action verbs.
- Impact.
- Metrics.
- Technology keywords.

### Education

Users can add:

- Institution.
- Degree.
- Field of study.
- Start/end dates.
- Grade/CGPA.
- Relevant coursework.

### Projects

Users can import from Projects module:

- Project title.
- Description.
- Tech stack.
- GitHub link.
- Live demo.
- Bullet highlights.

Users should be able to choose which projects appear on each resume.

### Certifications

Users can include:

- Certification name.
- Issuer.
- Issue date.
- Credential URL.

### Achievements

Users can add:

- Awards.
- Hackathons.
- Publications.
- Leadership.
- Competitive programming highlights.
- Academic achievements.

### Responsive UI

The resume builder must support:

- Desktop split editor/preview layout.
- Tablet stacked editor with preview toggle.
- Mobile wizard or tabbed layout.
- Touch-friendly controls.
- No horizontal overflow in editor.

## 3. Non-Functional Requirements

- Resume data must be user-scoped and secure.
- Resume rendering must be deterministic.
- PDF generation must be reliable and production-friendly.
- Autosave should be possible in future.
- Resume editor components must be reusable.
- Large resume data should not cause UI lag.
- ATS scoring should be explainable and transparent.
- Version history should avoid excessive storage growth.
- All list endpoints must support pagination where needed.
- Exported files must not expose private storage paths.

## 4. Database Tables

### `resume_templates`

Stores available templates.

```text
id BIGINT PK
template_key VARCHAR(80) UNIQUE NOT NULL
name VARCHAR(120) NOT NULL
description VARCHAR(255)
category VARCHAR(80)
is_ats_friendly BOOLEAN DEFAULT TRUE
is_active BOOLEAN DEFAULT TRUE
created_at DATETIME
updated_at DATETIME
```

Seed examples:

```text
classic
modern
compact
technical
fresher
internship
```

### `resumes`

Stores resume identity.

```text
id BIGINT PK
user_id BIGINT FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
title VARCHAR(140) NOT NULL
target_role VARCHAR(120)
template_id BIGINT FK resume_templates(id)
is_primary BOOLEAN DEFAULT FALSE
current_version_id BIGINT NULL
ats_score INT DEFAULT 0
pdf_url VARCHAR(500)
pdf_public_id VARCHAR(255)
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

Indexes:

```text
idx_resumes_user_id
idx_resumes_user_primary
idx_resumes_template_id
uk_resumes_public_id
```

### `resume_versions`

Stores snapshot versions.

```text
id BIGINT PK
resume_id BIGINT FK resumes(id)
version_number INT NOT NULL
title VARCHAR(140) NOT NULL
template_key VARCHAR(80) NOT NULL
snapshot_json JSON NOT NULL
ats_score INT DEFAULT 0
change_summary VARCHAR(255)
created_at DATETIME
created_by BIGINT FK users(id)
```

Indexes:

```text
idx_resume_versions_resume_id
uk_resume_version_number
```

### `resume_sections`

Stores section ordering and visibility.

```text
id BIGINT PK
resume_id BIGINT FK resumes(id)
section_key VARCHAR(80) NOT NULL
section_title VARCHAR(120) NOT NULL
display_order INT NOT NULL
visible BOOLEAN DEFAULT TRUE
locked BOOLEAN DEFAULT FALSE
created_at DATETIME
updated_at DATETIME
```

Recommended section keys:

```text
SUMMARY
SKILLS
EXPERIENCE
EDUCATION
PROJECTS
CERTIFICATIONS
ACHIEVEMENTS
LINKS
```

### `resume_content_blocks`

Stores resume-specific content for structured sections.

```text
id BIGINT PK
resume_id BIGINT FK resumes(id)
section_id BIGINT FK resume_sections(id)
source_type VARCHAR(80)
source_id BIGINT NULL
content_json JSON NOT NULL
display_order INT NOT NULL
visible BOOLEAN DEFAULT TRUE
created_at DATETIME
updated_at DATETIME
```

`source_type` examples:

```text
PROFILE
SKILL
EXPERIENCE
EDUCATION
PROJECT
CERTIFICATION
ACHIEVEMENT
CUSTOM
```

### `resume_ats_scores`

Stores score details and recommendations.

```text
id BIGINT PK
resume_id BIGINT FK resumes(id)
version_id BIGINT NULL FK resume_versions(id)
overall_score INT NOT NULL
score_breakdown_json JSON NOT NULL
recommendations_json JSON NOT NULL
target_role VARCHAR(120)
job_description_hash VARCHAR(255)
created_at DATETIME
```

### `resume_exports`

Tracks PDF exports.

```text
id BIGINT PK
resume_id BIGINT FK resumes(id)
version_id BIGINT NULL FK resume_versions(id)
file_url VARCHAR(500) NOT NULL
file_public_id VARCHAR(255)
file_name VARCHAR(180)
export_status VARCHAR(40) NOT NULL
created_at DATETIME
```

## 5. API Endpoints

Base path:

```text
/api/v1/resumes
```

### Templates

```text
GET /api/v1/resume-templates
GET /api/v1/resume-templates/{templateKey}
```

### Resume CRUD

```text
POST   /api/v1/resumes
GET    /api/v1/resumes
GET    /api/v1/resumes/{resumeId}
PUT    /api/v1/resumes/{resumeId}
DELETE /api/v1/resumes/{resumeId}
PATCH  /api/v1/resumes/{resumeId}/primary
```

### Sections and Content

```text
PUT   /api/v1/resumes/{resumeId}/sections/reorder
PATCH /api/v1/resumes/{resumeId}/sections/{sectionId}/visibility
PUT   /api/v1/resumes/{resumeId}/content
POST  /api/v1/resumes/{resumeId}/import-profile-data
```

### Live Preview

```text
GET  /api/v1/resumes/{resumeId}/preview
POST /api/v1/resumes/preview
```

### PDF Export

```text
POST /api/v1/resumes/{resumeId}/export/pdf
GET  /api/v1/resumes/{resumeId}/exports
```

### Version History

```text
GET  /api/v1/resumes/{resumeId}/versions
POST /api/v1/resumes/{resumeId}/versions
GET  /api/v1/resumes/{resumeId}/versions/{versionId}
POST /api/v1/resumes/{resumeId}/versions/{versionId}/restore
```

### ATS Score

```text
POST /api/v1/resumes/{resumeId}/ats-score
GET  /api/v1/resumes/{resumeId}/ats-score/latest
```

## 6. API Contracts

### Create Resume Request

```json
{
  "title": "Full Stack Developer Resume",
  "targetRole": "Full Stack Developer",
  "templateKey": "technical",
  "importProfileData": true
}
```

### Resume Detail Response

```json
{
  "id": "res_01J0",
  "title": "Full Stack Developer Resume",
  "targetRole": "Full Stack Developer",
  "templateKey": "technical",
  "isPrimary": true,
  "atsScore": 82,
  "sections": [
    {
      "id": "sec_01",
      "sectionKey": "SUMMARY",
      "sectionTitle": "Summary",
      "displayOrder": 1,
      "visible": true
    }
  ],
  "contentBlocks": [
    {
      "id": "blk_01",
      "sectionKey": "PROJECTS",
      "sourceType": "PROJECT",
      "sourceId": "prj_01J0",
      "content": {
        "title": "CareerNest",
        "description": "Built a full stack SaaS platform for career management.",
        "technologies": ["React", "Spring Boot", "MySQL"]
      },
      "displayOrder": 1,
      "visible": true
    }
  ]
}
```

### Reorder Sections Request

```json
{
  "sections": [
    { "sectionId": "sec_01", "displayOrder": 1 },
    { "sectionId": "sec_02", "displayOrder": 2 },
    { "sectionId": "sec_03", "displayOrder": 3 }
  ]
}
```

### ATS Score Response

```json
{
  "overallScore": 82,
  "breakdown": [
    {
      "category": "Skills",
      "score": 90,
      "maxScore": 100
    },
    {
      "category": "Projects",
      "score": 85,
      "maxScore": 100
    }
  ],
  "recommendations": [
    {
      "severity": "HIGH",
      "title": "Add quantified impact",
      "description": "Add measurable outcomes to at least two project or experience bullet points."
    }
  ]
}
```

## 7. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/resume/
    controller/
      ResumeController.java
      ResumeTemplateController.java
      ResumeExportController.java
      ResumeVersionController.java
      AtsScoreController.java
    dto/
      CreateResumeRequest.java
      UpdateResumeRequest.java
      ResumeResponse.java
      ResumeListItemResponse.java
      ResumeTemplateResponse.java
      ResumeSectionResponse.java
      ReorderResumeSectionsRequest.java
      ResumeContentRequest.java
      ResumePreviewResponse.java
      ResumeVersionResponse.java
      AtsScoreResponse.java
      AtsRecommendationResponse.java
      ResumeExportResponse.java
    entity/
      Resume.java
      ResumeTemplate.java
      ResumeSection.java
      ResumeContentBlock.java
      ResumeVersion.java
      ResumeAtsScore.java
      ResumeExport.java
      ResumeSectionKey.java
      ResumeExportStatus.java
    repository/
      ResumeRepository.java
      ResumeTemplateRepository.java
      ResumeSectionRepository.java
      ResumeContentBlockRepository.java
      ResumeVersionRepository.java
      ResumeAtsScoreRepository.java
      ResumeExportRepository.java
    service/
      ResumeService.java
      ResumeServiceImpl.java
      ResumeTemplateService.java
      ResumePreviewService.java
      ResumeExportService.java
      ResumeVersionService.java
      AtsScoringService.java
      ResumeImportService.java
    mapper/
      ResumeMapper.java
```

## 8. Frontend Folder Structure

```text
frontend/
  src/
    features/
      resumes/
        pages/
          ResumesPage.jsx
          ResumeBuilderPage.jsx
          ResumePreviewPage.jsx
          ResumeVersionsPage.jsx
        components/
          ResumeCard.jsx
          ResumeTemplatePicker.jsx
          ResumeBuilderShell.jsx
          ResumeEditorPanel.jsx
          ResumePreviewPanel.jsx
          ResumeSectionManager.jsx
          ResumeSectionItem.jsx
          ResumeDragDropList.jsx
          ResumeMobileSectionControls.jsx
          AtsScorePanel.jsx
          AtsRecommendationList.jsx
          ResumeExportButton.jsx
          ResumeVersionHistory.jsx
          SkillsSectionEditor.jsx
          ExperienceSectionEditor.jsx
          EducationSectionEditor.jsx
          ProjectsSectionEditor.jsx
          CertificationsSectionEditor.jsx
          AchievementsSectionEditor.jsx
        templates/
          ClassicTemplate.jsx
          ModernTemplate.jsx
          CompactTemplate.jsx
          TechnicalTemplate.jsx
          FresherTemplate.jsx
          InternshipTemplate.jsx
        services/
          resumeService.js
        hooks/
          useResumes.js
          useResumeBuilder.js
          useResumePreview.js
          useAtsScore.js
          useResumeVersions.js
        validation/
          resumeSchemas.js
        utils/
          resumeFormatters.js
          resumeTemplateRegistry.js
```

## 9. UI Design

### Resume List Page

Shows:

- Create Resume button.
- Resume cards.
- Primary resume marker.
- Template name.
- ATS score.
- Last updated date.
- Download PDF action.
- Duplicate action.

### Resume Builder Desktop Layout

```text
Topbar: Resume title, template, ATS score, save, download
Left Panel: Sections and editor forms
Right Panel: Live preview
```

### Resume Builder Mobile Layout

Use tabs:

```text
Edit | Sections | Preview | Score
```

### Drag and Drop UX

- Desktop: drag handle on section list.
- Keyboard accessible reorder should be planned.
- Mobile: up/down buttons.
- Changes should save explicitly or autosave in future.

### Live Preview UX

- Preview should look like a resume page.
- Use stable page width on desktop.
- Mobile preview can be zoomed/scaled.
- Show page boundary and export-safe spacing.

### ATS Score Panel

Show:

- Circular or linear score display.
- Score category breakdown.
- Recommendation list.
- Quick fix actions where possible.

## 10. Security Considerations

- All resume APIs require authentication.
- Backend must derive user identity from JWT.
- Users can access only their own resumes and versions.
- Resume export must validate ownership before generating PDF.
- Avoid rendering unsanitized HTML in preview.
- Do not expose internal database IDs where public IDs are available.
- Validate all JSON content blocks.
- Limit resume count per user in future plan tiers.
- PDF URLs should be secure and not expose private storage configuration.

## 11. Validation Rules

### Resume

```text
title: required, 3-140 characters
targetRole: optional, max 120 characters
templateKey: required, must exist and be active
sections: valid known section keys
displayOrder: positive integer
```

### Summary

```text
summary: recommended 2-4 lines, max 700 characters
```

### Experience

```text
company: required
roleTitle: required
startDate: required
endDate: optional, must be after startDate
bulletPoints: max 8 per experience
bulletPoint length: max 260 characters
```

### Education

```text
institution: required
degree: required
fieldOfStudy: optional
grade: optional
```

### Projects

```text
title: required
description: recommended
technologies: max 12 per project in resume display
githubUrl/liveDemoUrl: valid URL
```

### Certifications

```text
title: required
issuer: required
credentialUrl: optional valid URL
```

### Achievements

```text
title: required
description: optional, max 300 characters
date: optional
```

## 12. ATS Scoring Model

### Score Categories

```text
Contact and Profile: 15
Summary: 10
Skills: 20
Experience: 15
Projects: 15
Education: 10
Certifications and Achievements: 5
Keywords: 10
Formatting: 10
```

The score can exceed 100 in raw calculation but should be capped at 100.

### Recommendation Examples

- Add a professional summary.
- Add at least 8 relevant skills.
- Add measurable impact to project bullets.
- Include GitHub or portfolio links.
- Add missing education dates.
- Reduce overly long bullet points.
- Add target role keywords.
- Avoid unsupported symbols or image-heavy formatting.

## 13. PDF Generation Strategy

### Recommended Backend Strategy

Generate PDF on backend using HTML-to-PDF rendering.

Requirements:

- Template HTML generated from structured resume DTO.
- CSS designed for print.
- PDF text must remain selectable.
- Store export metadata.

### Frontend Preview Strategy

Frontend templates should use the same design tokens and layout rules as export templates. The backend export may use server-side equivalent HTML to guarantee reliable PDF generation.

### Future Enhancement

- Async export queue for large workloads.
- PDF generation status polling.
- Watermark or premium templates.

## 14. Version History Strategy

Version snapshots should store a full structured JSON copy of the resume at that moment.

Create versions when:

- User manually saves version.
- User changes template.
- User exports PDF.
- User restores an older version.

Avoid creating a version on every keystroke. Autosave drafts can be separate in a future enhancement.

## 15. Future Scalability

The Resume Builder should support:

- AI bullet improvement.
- Job description keyword matching.
- Resume tailoring per job application.
- Cover letter generation.
- Public resume sharing.
- Recruiter-friendly portfolio export.
- Premium templates.
- Resume parser import.
- LinkedIn import.
- Multi-language resumes.
- Team review/comments.

## 16. Industry Best Practices

- Keep resume content structured, not only HTML blobs.
- Use template registry pattern on frontend.
- Use backend DTOs for export-ready resume shape.
- Keep ATS scoring explainable.
- Avoid exposing JPA entities.
- Use optimistic locking in future for concurrent editing.
- Use version snapshots before destructive restore.
- Use print CSS for PDF reliability.
- Keep mobile editor simpler than desktop.
- Reuse existing profile/projects/certifications data where possible.

## 17. Testing Strategy

### Backend Tests

- Create resume with valid template.
- Reject inactive template.
- User cannot access another user's resume.
- Section reorder persists correctly.
- Version snapshot captures full resume.
- Restore version updates current resume.
- ATS score handles incomplete resume.
- PDF export requires ownership.
- Deleted resume excluded from lists.

### Frontend Tests

- Resume list renders cards.
- Template picker updates preview.
- Drag and drop reorder updates section order.
- Mobile section controls reorder correctly.
- Live preview updates after field changes.
- ATS score panel renders recommendations.
- PDF download action handles loading and errors.
- Version restore requires confirmation.

### Visual QA

- Desktop split layout at 1440px.
- Laptop at 1280px.
- Tablet at 768px.
- Mobile at 390px.
- PDF preview print layout.
- Light and dark app shell, while resume page remains print-friendly.

## 18. Swagger Documentation

Swagger group:

```text
Resume Builder
```

Document:

- Template endpoints.
- Resume CRUD.
- Section reorder.
- Content save.
- Preview.
- PDF export.
- Version history.
- ATS scoring.

Each endpoint must include:

- Auth requirement.
- Request schema.
- Response schema.
- Validation errors.
- Ownership error examples.

## 19. Implementation Order After Approval

1. Create resume template, resume, section, content, version, ATS score, and export entities.
2. Seed resume templates.
3. Create repositories.
4. Define DTOs and validation.
5. Build resume mapper.
6. Implement resume CRUD service.
7. Implement section reorder and content save.
8. Implement profile/project data import.
9. Implement version history.
10. Implement ATS scoring service.
11. Implement PDF export service.
12. Add controllers and Swagger docs.
13. Add backend tests.
14. Build frontend template registry.
15. Build resume list and template picker.
16. Build resume builder shell.
17. Build drag and drop section manager.
18. Build live preview.
19. Build ATS score panel.
20. Build PDF download and version history UI.
21. Complete responsive polish.

## 20. Approval Gate

No ATS Resume Builder implementation should begin until this architecture is approved.

After approval, implementation should begin with backend resume entities, template seeding, DTOs, repositories, and secure resume CRUD APIs.
