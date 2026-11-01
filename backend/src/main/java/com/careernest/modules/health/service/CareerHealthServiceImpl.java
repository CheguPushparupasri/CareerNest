package com.careernest.modules.health.service;

import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.certification.repository.CertificationRepository;
import com.careernest.modules.coding.repository.CodingProfileRepository;
import com.careernest.modules.health.dto.CareerHealthResponse;
import com.careernest.modules.internship.repository.InternshipApplicationRepository;
import com.careernest.modules.learning.repository.LearningCourseRepository;
import com.careernest.modules.profile.entity.UserProfile;
import com.careernest.modules.profile.repository.UserProfileRepository;
import com.careernest.modules.project.repository.ProjectRepository;
import com.careernest.modules.resume.repository.ResumeRepository;
import com.careernest.modules.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CareerHealthServiceImpl implements CareerHealthService {
    private final UserProfileRepository userProfileRepository;
    private final ResumeRepository resumeRepository;
    private final ProjectRepository projectRepository;
    private final CertificationRepository certificationRepository;
    private final LearningCourseRepository learningCourseRepository;
    private final CodingProfileRepository codingProfileRepository;
    private final InternshipApplicationRepository internshipApplicationRepository;

    public CareerHealthServiceImpl(UserProfileRepository userProfileRepository, ResumeRepository resumeRepository,
                                   ProjectRepository projectRepository, CertificationRepository certificationRepository,
                                   LearningCourseRepository learningCourseRepository, CodingProfileRepository codingProfileRepository,
                                   InternshipApplicationRepository internshipApplicationRepository) {
        this.userProfileRepository = userProfileRepository;
        this.resumeRepository = resumeRepository;
        this.projectRepository = projectRepository;
        this.certificationRepository = certificationRepository;
        this.learningCourseRepository = learningCourseRepository;
        this.codingProfileRepository = codingProfileRepository;
        this.internshipApplicationRepository = internshipApplicationRepository;
    }

    @Override
    public CareerHealthResponse getHealth() {
        User user = currentUser();
        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);

        long resumeCount = resumeRepository.countByUserId(user.getId());
        long projectCount = projectRepository.countByUserId(user.getId());
        long certificationCount = certificationRepository.countByUserId(user.getId());
        long learningCount = learningCourseRepository.countByUserId(user.getId());
        long codingCount = codingProfileRepository.countByUserId(user.getId());
        long applicationCount = internshipApplicationRepository.countByUserId(user.getId());

        int resumeScore = scoreFromCount(resumeCount, 40, 1);
        int projectScore = scoreFromCount(projectCount, 25, 1);
        int certificationScore = scoreFromCount(certificationCount, 20, 1);
        int learningScore = scoreFromCount(learningCount, 10, 1);
        int codingScore = scoreFromCount(codingCount, 15, 1);
        int skillsScore = calculateSkillsScore(profile, certificationCount, codingCount);
        int profileScore = calculateProfileScore(user, profile);
        int internshipScore = scoreFromCount(applicationCount, 20, 1);

        int placementReadiness = Math.min(100,
                (resumeScore / 4) + (projectScore / 4) + (certificationScore / 5) + (profileScore / 3) + (internshipScore / 5));
        int interviewReadiness = Math.min(100,
                (resumeScore / 3) + (projectScore / 3) + (skillsScore / 4) + (codingScore / 5) + (applicationCount > 0 ? 15 : 0));

        int overallScore = Math.round(
                (resumeScore * 0.2f) + (projectScore * 0.18f) + (certificationScore * 0.12f)
                        + (learningScore * 0.12f) + (codingScore * 0.1f) + (skillsScore * 0.13f)
                        + (profileScore * 0.15f));

        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<String> roadmap = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        if (resumeScore >= 60) strengths.add("Resume foundation is solid");
        else weaknesses.add("Resume needs stronger ATS optimization");

        if (projectScore >= 50) strengths.add("Project portfolio is growing");
        else weaknesses.add("Add more practical projects with measurable outcomes");

        if (certificationScore >= 40) strengths.add("Credentials are being built");
        else weaknesses.add("Show more verifiable certifications");

        if (learningScore >= 50) strengths.add("Learning momentum is consistent");
        else weaknesses.add("Create a steadier weekly learning cadence");

        if (codingScore >= 40) strengths.add("Coding profiles are linked and active");
        else weaknesses.add("Link LeetCode, GitHub, or HackerRank profiles");

        if (skillsScore >= 60) strengths.add("Skill visibility is strong across your profile");
        else weaknesses.add("Document technical and soft skills more clearly");

        if (profileScore >= 70) strengths.add("Profile completion supports recruiter trust");
        else weaknesses.add("Complete headline, education, and social links");

        if (applicationCount >= 2) strengths.add("Internship pipeline activity is underway");
        else weaknesses.add("Start tracking internship applications and follow-ups");

        if (overallScore < 70) {
            roadmap.add("Complete your profile and primary resume this week");
            roadmap.add("Add one portfolio project with deployment and README details");
            roadmap.add("Earn or upload one relevant certification");
            roadmap.add("Track learning goals and apply to two targeted internships");
        } else {
            roadmap.add("Refine your primary resume for target roles");
            roadmap.add("Prepare STAR stories from top projects");
            roadmap.add("Schedule mock interviews and coding practice sessions");
        }

        if (resumeScore < 60) recommendations.add("Build an ATS-friendly resume with quantified impact bullets.");
        if (projectScore < 50) recommendations.add("Publish one polished project with live demo and tech stack.");
        if (certificationScore < 40) recommendations.add("Earn a role-relevant certification and add it to CareerNest.");
        if (learningScore < 50) recommendations.add("Set a 2-week learning sprint with daily progress tracking.");
        if (codingScore < 40) recommendations.add("Connect coding profiles to showcase problem-solving consistency.");
        if (skillsScore < 60) recommendations.add("Align skills with your target role and highlight them on your resume.");
        if (interviewReadiness < 65) recommendations.add("Practice behavioral and technical interview questions weekly.");
        if (recommendations.isEmpty()) {
            recommendations.add("Maintain momentum — your career health is strong. Target senior internships next.");
        }

        return new CareerHealthResponse(overallScore, resumeScore, skillsScore, placementReadiness, interviewReadiness,
                learningScore, codingScore, projectScore, recommendations, strengths, weaknesses, roadmap);
    }

    private int scoreFromCount(long count, int weight, int unitSize) {
        return (int) Math.min(100, count * weight / (float) unitSize);
    }

    private int calculateProfileScore(User user, UserProfile profile) {
        int score = 0;
        if (notBlank(user.getFullName())) score += 10;
        if (user.isEmailVerified()) score += 15;
        if (user.isProfileSetupCompleted()) score += 15;
        if (profile != null && notBlank(profile.getHeadline())) score += 15;
        if (profile != null && notBlank(profile.getLocation())) score += 10;
        if (profile != null && notBlank(profile.getEducationSummary())) score += 15;
        if (profile != null && notBlank(profile.getGithubUrl())) score += 10;
        if (profile != null && notBlank(profile.getLinkedinUrl())) score += 10;
        return Math.min(score, 100);
    }

    private int calculateSkillsScore(UserProfile profile, long certificationCount, long codingCount) {
        int score = (int) Math.min(50, certificationCount * 15 + codingCount * 10);
        if (profile != null && notBlank(profile.getHeadline())) score += 20;
        if (profile != null && notBlank(profile.getEducationSummary())) score += 15;
        if (profile != null && notBlank(profile.getGithubUrl())) score += 15;
        return Math.min(score, 100);
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }
}
