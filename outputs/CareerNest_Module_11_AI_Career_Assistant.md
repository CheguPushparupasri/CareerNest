# CareerNest Module 11: AI Career Assistant

## 1. Purpose of the Module

The AI Career Assistant helps students and freshers make better career decisions using their structured CareerNest data. It supports AI resume review, career suggestions, skill gap analysis, interview questions, roadmap generation, mock interviews, chat assistance, and job recommendations.

This module must be designed as a secure AI orchestration layer, not as random prompt calls from the frontend. It should protect user data, control costs, produce structured outputs, and integrate with CareerNest modules such as Resume Builder, Learning Tracker, Projects, Certifications, Coding Profiles, Internship Applications, and Career Timeline.

## 2. Functional Requirements

### AI Resume Review

Users can request AI feedback on a selected resume.

The review should include:

- Overall resume score.
- ATS readability feedback.
- Section-by-section suggestions.
- Missing skills or keywords.
- Weak bullet points.
- Grammar and clarity improvements.
- Role-specific tailoring suggestions.
- Suggested rewritten bullet points.

Inputs:

- Resume content.
- Target role.
- Optional job description.
- User profile/projects/skills.

Outputs:

- Structured review.
- Prioritized action items.
- Suggested edits.

### AI Career Suggestions

The assistant should suggest career paths based on:

- Skills.
- Projects.
- Certifications.
- Coding profiles.
- Learning progress.
- Internship applications.
- Career goals.

Examples:

- Full Stack Developer.
- Backend Developer.
- Frontend Developer.
- Data Analyst.
- Cloud Engineer.
- QA Automation Engineer.

Suggestions should include:

- Fit score.
- Reasoning.
- Missing skills.
- Recommended next steps.
- Suggested projects/certifications.

### Skill Gap Analysis

Compare user profile against:

- Target role.
- Job description.
- Career path template.
- Internship requirements.

Output:

- Existing matching skills.
- Missing must-have skills.
- Nice-to-have skills.
- Priority.
- Learning resources.
- Estimated effort.

### Interview Questions

Generate interview questions by:

- Role.
- Skill.
- Difficulty.
- Company type.
- Experience level.
- Resume/project context.

Question types:

- Technical.
- DSA.
- System design basics.
- Project deep-dive.
- Behavioral.
- HR.
- Aptitude.

Each question can include:

- Expected answer outline.
- Evaluation rubric.
- Follow-up questions.

### Roadmap Generator

Generate a personalized roadmap for a target role.

Roadmap should include:

- Skill modules.
- Learning milestones.
- Projects to build.
- Certifications to consider.
- Interview preparation.
- Weekly plan.
- Estimated timeline.

Generated roadmap can be saved into the Learning Tracker or Career Roadmap module.

### Mock Interview

Mock interview flow:

- User selects role and difficulty.
- AI asks questions one by one.
- User answers in text initially.
- AI gives feedback after each answer or at the end.
- AI scores clarity, correctness, depth, and confidence.

Future support:

- Voice input.
- Video interview simulation.
- Timed rounds.

### Chat Assistant

Users can chat with a CareerNest assistant about:

- Resume improvements.
- Career path confusion.
- Interview preparation.
- Project ideas.
- Learning plans.
- Application follow-ups.
- Profile optimization.

The assistant should use user context only after explicit authenticated access through backend services.

### Job Recommendation

Recommend jobs/internships based on:

- User skills.
- Target role.
- Projects.
- Resume.
- Location preference.
- Work mode preference.
- Application history.
- Skill gap.

Initial implementation can recommend from internally stored jobs. Future implementation can integrate external job APIs.

## 3. Non-Functional Requirements

- All AI endpoints must require authentication.
- User data must be scoped to the authenticated user.
- AI calls must happen from backend, never directly from frontend.
- Prompts must be versioned.
- Outputs should be structured JSON where possible.
- AI responses must be stored with metadata for history and debugging.
- Sensitive data must not be logged.
- Cost and rate limits must be enforced.
- AI output must be treated as suggestions, not absolute truth.
- The system must degrade gracefully when AI provider is unavailable.
- Long-running operations should be async-ready.

## 4. Database Tables

### `ai_conversations`

```text
id BIGINT PK
user_id BIGINT FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
conversation_type VARCHAR(80) NOT NULL
title VARCHAR(180)
context_summary TEXT
created_at DATETIME
updated_at DATETIME
deleted_at DATETIME NULL
```

Conversation types:

```text
CAREER_CHAT
MOCK_INTERVIEW
RESUME_REVIEW
ROADMAP_GENERATION
SKILL_GAP_ANALYSIS
JOB_RECOMMENDATION
```

### `ai_messages`

```text
id BIGINT PK
conversation_id BIGINT FK ai_conversations(id)
role VARCHAR(40) NOT NULL
content TEXT NOT NULL
structured_content_json JSON
token_count INT
created_at DATETIME
```

Roles:

```text
USER
ASSISTANT
SYSTEM
TOOL
```

### `ai_requests`

Tracks each AI operation.

```text
id BIGINT PK
user_id BIGINT FK users(id)
conversation_id BIGINT NULL FK ai_conversations(id)
request_type VARCHAR(80) NOT NULL
provider VARCHAR(80)
model_name VARCHAR(120)
prompt_version VARCHAR(60)
input_tokens INT
output_tokens INT
status VARCHAR(40) NOT NULL
error_message VARCHAR(500)
created_at DATETIME
completed_at DATETIME NULL
```

### `ai_resume_reviews`

```text
id BIGINT PK
user_id BIGINT FK users(id)
resume_id BIGINT FK resumes(id)
public_id VARCHAR(64) UNIQUE NOT NULL
target_role VARCHAR(120)
job_description TEXT
overall_score INT
ats_score INT
review_json JSON NOT NULL
created_at DATETIME
```

### `ai_skill_gap_reports`

```text
id BIGINT PK
user_id BIGINT FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
target_role VARCHAR(120) NOT NULL
job_description TEXT
matched_skills_json JSON
missing_skills_json JSON
recommendations_json JSON
created_at DATETIME
```

### `ai_mock_interviews`

```text
id BIGINT PK
user_id BIGINT FK users(id)
conversation_id BIGINT FK ai_conversations(id)
public_id VARCHAR(64) UNIQUE NOT NULL
target_role VARCHAR(120)
difficulty VARCHAR(40)
status VARCHAR(40) NOT NULL
overall_score INT NULL
feedback_json JSON
started_at DATETIME
completed_at DATETIME NULL
```

### `ai_job_recommendations`

```text
id BIGINT PK
user_id BIGINT FK users(id)
public_id VARCHAR(64) UNIQUE NOT NULL
recommendation_json JSON NOT NULL
source VARCHAR(80)
created_at DATETIME
```

## 5. API Endpoints

Base path:

```text
/api/v1/ai
```

### Resume Review

```text
POST /api/v1/ai/resume-review
GET  /api/v1/ai/resume-review/history
GET  /api/v1/ai/resume-review/{reviewId}
```

### Career Suggestions

```text
POST /api/v1/ai/career-suggestions
GET  /api/v1/ai/career-suggestions/latest
```

### Skill Gap Analysis

```text
POST /api/v1/ai/skill-gap-analysis
GET  /api/v1/ai/skill-gap-analysis/history
GET  /api/v1/ai/skill-gap-analysis/{reportId}
```

### Interview Questions

```text
POST /api/v1/ai/interview-questions
```

### Roadmap Generator

```text
POST /api/v1/ai/roadmap-generator
POST /api/v1/ai/roadmap-generator/{roadmapId}/save
```

### Mock Interview

```text
POST /api/v1/ai/mock-interviews
GET  /api/v1/ai/mock-interviews/{mockInterviewId}
POST /api/v1/ai/mock-interviews/{mockInterviewId}/answer
POST /api/v1/ai/mock-interviews/{mockInterviewId}/complete
```

### Chat Assistant

```text
POST /api/v1/ai/conversations
GET  /api/v1/ai/conversations
GET  /api/v1/ai/conversations/{conversationId}
POST /api/v1/ai/conversations/{conversationId}/messages
DELETE /api/v1/ai/conversations/{conversationId}
```

### Job Recommendation

```text
POST /api/v1/ai/job-recommendations
GET  /api/v1/ai/job-recommendations/latest
```

## 6. API Contracts

### Resume Review Request

```json
{
  "resumeId": "res_01J0",
  "targetRole": "Full Stack Developer",
  "jobDescription": "We are looking for React, Spring Boot, SQL, and REST API experience.",
  "includeProfileContext": true,
  "includeProjects": true,
  "includeCertifications": true
}
```

### Resume Review Response

```json
{
  "reviewId": "airr_01J0",
  "overallScore": 82,
  "atsScore": 78,
  "summary": "The resume is strong for fresher full stack roles but needs more quantified project impact.",
  "sectionFeedback": [
    {
      "section": "Projects",
      "score": 80,
      "feedback": "Projects are relevant but bullet points need measurable outcomes."
    }
  ],
  "recommendations": [
    {
      "priority": "HIGH",
      "title": "Add quantified impact",
      "description": "Add metrics such as users, response time improvement, or deployment outcomes."
    }
  ],
  "suggestedBullets": [
    "Built REST APIs with Spring Boot and MySQL to manage student career data across resumes, projects, and applications."
  ]
}
```

### Skill Gap Request

```json
{
  "targetRole": "Backend Developer Intern",
  "jobDescription": "Java, Spring Boot, SQL, REST APIs, Git, Docker preferred.",
  "includeLearningProgress": true,
  "includeCodingProfiles": true
}
```

### Skill Gap Response

```json
{
  "targetRole": "Backend Developer Intern",
  "fitScore": 74,
  "matchedSkills": ["Java", "SQL", "Git"],
  "missingSkills": [
    {
      "skill": "Spring Security",
      "priority": "HIGH",
      "recommendedAction": "Complete a Spring Security JWT project."
    }
  ],
  "recommendedRoadmap": [
    {
      "week": 1,
      "focus": "Spring Boot REST APIs",
      "tasks": ["Build CRUD APIs", "Add validation", "Write tests"]
    }
  ]
}
```

### Chat Message Request

```json
{
  "message": "How can I improve my resume for Java backend internships?",
  "contextOptions": {
    "includeResume": true,
    "includeProjects": true,
    "includeLearning": true,
    "includeApplications": false
  }
}
```

## 7. Backend Folder Structure

```text
backend/
  src/main/java/com/careernest/modules/ai/
    controller/
      AiResumeReviewController.java
      AiCareerSuggestionController.java
      AiSkillGapController.java
      AiInterviewController.java
      AiRoadmapController.java
      AiChatController.java
      AiJobRecommendationController.java
    dto/
      AiResumeReviewRequest.java
      AiResumeReviewResponse.java
      AiCareerSuggestionRequest.java
      AiCareerSuggestionResponse.java
      AiSkillGapRequest.java
      AiSkillGapResponse.java
      AiInterviewQuestionRequest.java
      AiInterviewQuestionResponse.java
      AiRoadmapRequest.java
      AiRoadmapResponse.java
      AiMockInterviewRequest.java
      AiMockInterviewResponse.java
      AiChatMessageRequest.java
      AiChatMessageResponse.java
      AiJobRecommendationRequest.java
      AiJobRecommendationResponse.java
    entity/
      AiConversation.java
      AiMessage.java
      AiRequest.java
      AiResumeReview.java
      AiSkillGapReport.java
      AiMockInterview.java
      AiJobRecommendation.java
    repository/
      AiConversationRepository.java
      AiMessageRepository.java
      AiRequestRepository.java
      AiResumeReviewRepository.java
      AiSkillGapReportRepository.java
      AiMockInterviewRepository.java
      AiJobRecommendationRepository.java
    service/
      AiGatewayService.java
      AiPromptService.java
      AiContextBuilderService.java
      AiResumeReviewService.java
      AiCareerSuggestionService.java
      AiSkillGapService.java
      AiInterviewQuestionService.java
      AiRoadmapGeneratorService.java
      AiMockInterviewService.java
      AiChatService.java
      AiJobRecommendationService.java
      AiUsageLimitService.java
    provider/
      AiProviderClient.java
      OpenAiProviderClient.java
      MockAiProviderClient.java
    prompt/
      PromptTemplateRegistry.java
      PromptVersion.java
    mapper/
      AiMapper.java
```

## 8. Frontend Folder Structure

```text
frontend/
  src/
    features/
      aiAssistant/
        pages/
          AiAssistantPage.jsx
          AiResumeReviewPage.jsx
          AiSkillGapPage.jsx
          AiRoadmapGeneratorPage.jsx
          AiMockInterviewPage.jsx
          AiJobRecommendationsPage.jsx
        components/
          AiChatPanel.jsx
          AiMessageBubble.jsx
          AiContextSelector.jsx
          AiResumeReviewPanel.jsx
          AiScoreCard.jsx
          AiRecommendationList.jsx
          SkillGapMatrix.jsx
          RoadmapPreview.jsx
          InterviewQuestionCard.jsx
          MockInterviewSession.jsx
          JobRecommendationCard.jsx
          AiLoadingState.jsx
          AiEmptyState.jsx
        services/
          aiAssistantService.js
        hooks/
          useAiChat.js
          useAiResumeReview.js
          useAiSkillGap.js
          useAiRoadmap.js
          useAiMockInterview.js
          useAiJobRecommendations.js
        validation/
          aiAssistantSchemas.js
        utils/
          aiFormatters.js
```

## 9. UI Design

### AI Assistant Home

Show AI tools as work-focused actions:

- Resume Review.
- Skill Gap Analysis.
- Career Suggestions.
- Roadmap Generator.
- Mock Interview.
- Interview Questions.
- Job Recommendations.
- Chat Assistant.

### Chat Assistant UI

- Two-column desktop layout:
  - Left: conversation list and context controls.
  - Right: chat panel.
- Mobile:
  - Full-screen chat.
  - Context settings in drawer.

### Resume Review UI

Show:

- Resume selector.
- Target role input.
- Optional job description field.
- Score cards.
- Section feedback.
- Recommendation list.
- Suggested bullet rewrites.

### Skill Gap UI

Show:

- Target role.
- Fit score.
- Matched skills.
- Missing skills.
- Priority matrix.
- Save to roadmap action.

### Mock Interview UI

Show:

- Interview setup.
- Question card.
- Answer input.
- Timer optional.
- Feedback panel.
- Final score summary.

### Responsive Strategy

- Desktop: two-column tool layouts.
- Tablet: stacked panels.
- Mobile: single workflow per screen.
- Use skeletons for AI loading states.
- Long AI output must be collapsible and scannable.

## 10. Security and Privacy Considerations

- AI calls must be backend-only.
- All AI endpoints require authentication.
- Backend derives user id from JWT.
- Users can only access their own conversations and reports.
- Context builder must fetch only authorized user data.
- Do not log raw prompts containing personal data.
- Do not log raw AI responses if they contain private data unless explicitly configured.
- Store AI metadata separately from sensitive prompt content where possible.
- Redact secrets, tokens, email verification links, and private notes.
- Provide a clear disclaimer that AI output is guidance.
- Add usage limits to prevent abuse and cost spikes.
- Future enterprise settings should allow AI data retention controls.

## 11. Prompt Architecture

Prompts should be:

- Versioned.
- Stored in backend code or database.
- Built from structured templates.
- Given only the context required for the task.
- Asked to return structured JSON when the UI needs structured rendering.

Prompt components:

```text
System instruction
Task instruction
User context summary
Selected module data
Output schema
Safety constraints
```

## 12. AI Provider Strategy

Use an `AiProviderClient` interface so the app can support:

- OpenAI or another production provider.
- Mock provider for local tests.
- Future provider fallback.

Provider configuration should come from environment variables:

```text
AI_PROVIDER
AI_API_KEY
AI_MODEL
AI_TIMEOUT_SECONDS
AI_MAX_INPUT_TOKENS
AI_MAX_OUTPUT_TOKENS
AI_DAILY_USER_LIMIT
```

## 13. Validation Rules

```text
resumeId: required for resume review, must belong to user
targetRole: required for career suggestions/skill gap/roadmap, 2-120 characters
jobDescription: optional, max 15000 characters
chat message: required, 1-4000 characters
mock interview difficulty: required enum EASY/MEDIUM/HARD
context options: booleans only
```

## 14. Dashboard Integration

Dashboard should show:

- Latest resume review score.
- Latest skill gap fit score.
- Suggested next career action.
- Upcoming mock interview practice.
- Recommended jobs count.

The dashboard must not call the AI provider directly. It should read saved summaries.

## 15. Future Scalability

The AI Career Assistant should support:

- Streaming chat responses.
- Voice mock interviews.
- Video interview feedback.
- Job description parsing.
- AI-generated cover letters.
- AI project idea generator.
- AI resume tailoring per application.
- Personal career coach memory with user controls.
- Mentor review workflows.
- Provider fallback and model routing.
- Async queue for long-running AI tasks.

## 16. Industry Best Practices

- Never call AI provider directly from frontend.
- Use structured output schemas.
- Version prompts.
- Store AI request metadata.
- Redact sensitive data.
- Use usage limits.
- Use mock provider for tests.
- Keep AI suggestions explainable.
- Allow users to review before applying AI changes.
- Keep deterministic business rules outside the AI model.

## 17. Testing Strategy

### Backend Tests

- User cannot access another user's AI conversation.
- Resume review validates resume ownership.
- Skill gap validates input length.
- Mock provider returns structured response.
- AI provider failure returns graceful error.
- Usage limit blocks excess requests.
- Prompt builder includes only selected context.
- Saved AI response can be fetched from history.

### Frontend Tests

- AI assistant page renders tool cards.
- Chat sends message and renders response.
- Loading states show during AI request.
- Resume review form validates required fields.
- Skill gap matrix renders missing skills.
- Mock interview answer flow works.
- Long AI output is readable on mobile.
- Error state is shown when AI request fails.

## 18. Swagger Documentation

Swagger group:

```text
AI Career Assistant
```

Document:

- Resume review.
- Career suggestions.
- Skill gap analysis.
- Interview questions.
- Roadmap generator.
- Mock interview.
- Chat assistant.
- Job recommendations.

Each endpoint must include:

- Authentication requirement.
- Request schema.
- Response schema.
- Validation examples.
- Ownership error examples.
- AI provider failure examples.
- Rate limit examples.

## 19. Implementation Order After Approval

1. Create AI entities and enums.
2. Create AI repositories.
3. Define DTOs and validation.
4. Build AI provider interface and mock provider.
5. Build prompt registry and prompt versions.
6. Build AI context builder service.
7. Build usage limit service.
8. Implement resume review service.
9. Implement skill gap service.
10. Implement career suggestion service.
11. Implement roadmap generator service.
12. Implement interview question and mock interview services.
13. Implement chat service.
14. Implement job recommendation service.
15. Add controllers and Swagger docs.
16. Add backend tests with mock provider.
17. Build frontend AI assistant home.
18. Build chat UI.
19. Build resume review UI.
20. Build skill gap and roadmap UI.
21. Build mock interview UI.
22. Build job recommendation UI.
23. Integrate dashboard saved summaries.
24. Complete responsive polish.

## 20. Approval Gate

No AI Career Assistant implementation should begin until this architecture is approved.

After approval, implementation should begin with backend AI provider abstraction, mock provider, request logging, prompt registry, and secure context builder before any real AI provider integration.
