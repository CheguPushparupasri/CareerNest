package com.careernest.modules.assistant.service;

import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.assistant.dto.AssistantRequest;
import com.careernest.modules.assistant.dto.AssistantResponse;
import com.careernest.modules.certification.repository.CertificationRepository;
import com.careernest.modules.coding.repository.CodingProfileRepository;
import com.careernest.modules.learning.repository.LearningCourseRepository;
import com.careernest.modules.profile.repository.UserProfileRepository;
import com.careernest.modules.project.repository.ProjectRepository;
import com.careernest.modules.resume.repository.ResumeRepository;
import com.careernest.modules.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AssistantServiceImpl implements AssistantService {
    private final ResumeRepository resumeRepository;
    private final ProjectRepository projectRepository;
    private final CertificationRepository certificationRepository;
    private final LearningCourseRepository learningCourseRepository;
    private final CodingProfileRepository codingProfileRepository;
    private final UserProfileRepository userProfileRepository;

    public AssistantServiceImpl(ResumeRepository resumeRepository, ProjectRepository projectRepository,
                                CertificationRepository certificationRepository, LearningCourseRepository learningCourseRepository,
                                CodingProfileRepository codingProfileRepository, UserProfileRepository userProfileRepository) {
        this.resumeRepository = resumeRepository;
        this.projectRepository = projectRepository;
        this.certificationRepository = certificationRepository;
        this.learningCourseRepository = learningCourseRepository;
        this.codingProfileRepository = codingProfileRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public AssistantResponse advise(AssistantRequest request) {
        User user = currentUser();
        String type = request.type() == null ? "CHAT" : request.type().toUpperCase(Locale.ROOT);
        String prompt = request.prompt().toLowerCase(Locale.ROOT);
        String targetRole = request.targetRole() != null && !request.targetRole().isBlank()
                ? request.targetRole() : "Software Engineer";

        return switch (type) {
            case "RESUME_REVIEW" -> resumeReview(user, targetRole);
            case "SKILL_GAP" -> skillGapAnalysis(user, targetRole);
            case "LEARNING" -> learningRecommendations(user, prompt);
            case "PROJECT" -> projectRecommendations(user, targetRole);
            case "ROADMAP" -> careerRoadmap(user, targetRole);
            case "INTERVIEW" -> mockInterviewGuidance(user, targetRole);
            case "PLACEMENT" -> placementGuidance(user);
            default -> chatGuidance(user, prompt, targetRole);
        };
    }

    private AssistantResponse resumeReview(User user, String targetRole) {
        long resumeCount = resumeRepository.countByUserId(user.getId());
        long projectCount = projectRepository.countByUserId(user.getId());
        List<String> actions = new ArrayList<>();
        actions.add("Lead with a 2-line summary tailored to " + targetRole + ".");
        actions.add("Replace generic bullets with metrics (%, time saved, users impacted).");
        actions.add("Mirror keywords from your target job description in skills and experience.");
        if (resumeCount == 0) actions.add("Create your first ATS resume in CareerNest.");
        if (projectCount < 2) actions.add("Add at least two projects with stack, role, and outcome.");
        return new AssistantResponse(
                "RESUME_REVIEW",
                resumeCount > 0 ? "Your resume exists — focus on role-specific tailoring and measurable impact."
                        : "You have not created a resume yet. Start with a clean ATS template.",
                "Prioritize clarity, relevance to " + targetRole + ", and quantified achievements in every section.",
                "Revise summary, experience bullets, and skills this week; then export and review ATS score.",
                actions
        );
    }

    private AssistantResponse skillGapAnalysis(User user, String targetRole) {
        long certs = certificationRepository.countByUserId(user.getId());
        long coding = codingProfileRepository.countByUserId(user.getId());
        long learning = learningCourseRepository.countByUserId(user.getId());
        List<String> actions = new ArrayList<>();
        actions.add("Map must-have skills for " + targetRole + " against your resume and projects.");
        actions.add("Close top 2 gaps with a focused 2-week learning sprint.");
        if (coding == 0) actions.add("Link a coding profile to demonstrate DSA consistency.");
        if (certs == 0) actions.add("Add one verifiable certification aligned with your target role.");
        if (learning < 2) actions.add("Track at least two in-progress courses in the Learning module.");
        return new AssistantResponse(
                "SKILL_GAP",
                "Skill gap analysis compares your current portfolio signals against " + targetRole + ".",
                "You likely have foundational skills; prioritize missing role-specific tools and proof of practice.",
                "Pick one must-have skill, one nice-to-have skill, and schedule daily practice.",
                actions
        );
    }

    private AssistantResponse learningRecommendations(User user, String prompt) {
        List<String> actions = List.of(
                "Choose one primary track (e.g. Spring Boot, React, or DSA).",
                "Set a weekly hour target and log progress in Learning Tracker.",
                "Ship one mini deliverable every two weeks (notes, lab, or small project)."
        );
        String focus = prompt.contains("react") ? "Frontend with React and modern tooling"
                : prompt.contains("java") || prompt.contains("spring") ? "Backend with Java and Spring Boot"
                : "Full-stack fundamentals with consistent weekly milestones";
        return new AssistantResponse(
                "LEARNING",
                "Learning recommendations should be narrow, measurable, and portfolio-linked.",
                "Focus on: " + focus + ".",
                "Start a 14-day sprint with daily study blocks and one portfolio artifact.",
                actions
        );
    }

    private AssistantResponse projectRecommendations(User user, String targetRole) {
        long projects = projectRepository.countByUserId(user.getId());
        List<String> actions = new ArrayList<>();
        if (projects == 0) {
            actions.add("Build a career dashboard or task manager with auth and CRUD.");
        } else {
            actions.add("Upgrade your strongest project with tests, README, and deployment.");
        }
        actions.add("Document problem statement, architecture, and your specific contributions.");
        actions.add("Add screenshots and link GitHub repo from the Projects module.");
        return new AssistantResponse(
                "PROJECT",
                "Projects should prove you can deliver real software, not just follow tutorials.",
                "Target a project that mirrors responsibilities of a " + targetRole + ".",
                "Pick one idea, define MVP scope, and publish within 3 weeks.",
                actions
        );
    }

    private AssistantResponse careerRoadmap(User user, String targetRole) {
        List<String> actions = List.of(
                "Month 1: Profile, resume, and one portfolio project.",
                "Month 2: Certifications, coding practice, and internship applications.",
                "Month 3: Mock interviews, referrals, and targeted role applications."
        );
        return new AssistantResponse(
                "ROADMAP",
                "A 90-day roadmap keeps your placement preparation structured and measurable.",
                "Roadmap for " + targetRole + ": foundation → proof → placement.",
                "Review progress every Sunday and adjust next week's priorities.",
                actions
        );
    }

    private AssistantResponse mockInterviewGuidance(User user, String targetRole) {
        List<String> actions = List.of(
                "Prepare 5 STAR stories from your best projects.",
                "Practice 10 technical questions for " + targetRole + " this week.",
                "Record one mock interview and review clarity, pace, and structure."
        );
        return new AssistantResponse(
                "INTERVIEW",
                "Interview readiness combines storytelling, technical depth, and consistent practice.",
                "Split prep into behavioral (STAR), technical (role-specific), and project deep-dives.",
                "Schedule two mock sessions before your next real interview.",
                actions
        );
    }

    private AssistantResponse placementGuidance(User user) {
        boolean profileReady = user.isProfileSetupCompleted();
        List<String> actions = new ArrayList<>();
        if (!profileReady) actions.add("Complete profile setup including education and social links.");
        actions.add("Maintain a weekly application target with follow-up reminders.");
        actions.add("Track applications in Internship Tracker and refine resume per role.");
        actions.add("Use Career Health score to prioritize weakest areas first.");
        return new AssistantResponse(
                "PLACEMENT",
                "Placement success comes from consistent applications plus portfolio quality.",
                profileReady ? "Your profile foundation is in place — focus on applications and interview prep."
                        : "Complete your profile before scaling application volume.",
                "Apply to 5 targeted roles this week and prepare one custom resume version.",
                actions
        );
    }

    private AssistantResponse chatGuidance(User user, String prompt, String targetRole) {
        if (prompt.contains("resume")) return resumeReview(user, targetRole);
        if (prompt.contains("project")) return projectRecommendations(user, targetRole);
        if (prompt.contains("interview")) return mockInterviewGuidance(user, targetRole);
        if (prompt.contains("skill") || prompt.contains("gap")) return skillGapAnalysis(user, targetRole);
        if (prompt.contains("learn")) return learningRecommendations(user, prompt);
        if (prompt.contains("roadmap") || prompt.contains("career")) return careerRoadmap(user, targetRole);
        if (prompt.contains("placement") || prompt.contains("intern")) return placementGuidance(user);

        userProfileRepository.findByUserId(user.getId());
        return new AssistantResponse(
                "CHAT",
                "You are building a strong foundation in your career journey.",
                "Focus on one high-impact action this week: resume, project, learning, or applications.",
                "Ask about resume review, skill gaps, projects, interviews, or placement guidance.",
                List.of(
                        "Try: 'Review my resume for " + targetRole + "'",
                        "Try: 'What skills am I missing for " + targetRole + "?'",
                        "Try: 'Give me a 90-day career roadmap'"
                )
        );
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }
}
