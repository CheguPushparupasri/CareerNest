package com.careernest.modules.dashboard.service;

import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.audit.repository.AuditLogRepository;
import com.careernest.modules.certification.entity.Certification;
import com.careernest.modules.certification.repository.CertificationRepository;
import com.careernest.modules.coding.repository.CodingProfileRepository;
import com.careernest.modules.dashboard.dto.*;
import com.careernest.modules.health.service.CareerHealthService;
import com.careernest.modules.internship.entity.InternshipApplication;
import com.careernest.modules.internship.repository.InternshipApplicationRepository;
import com.careernest.modules.learning.repository.LearningCourseRepository;
import com.careernest.modules.notification.service.NotificationService;
import com.careernest.modules.profile.entity.UserProfile;
import com.careernest.modules.profile.repository.UserProfileRepository;
import com.careernest.modules.project.entity.Project;
import com.careernest.modules.project.repository.ProjectRepository;
import com.careernest.modules.resume.repository.ResumeRepository;
import com.careernest.modules.timeline.entity.TimelineEntry;
import com.careernest.modules.timeline.repository.TimelineEntryRepository;
import com.careernest.modules.user.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final UserProfileRepository userProfileRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationService notificationService;
    private final ProjectRepository projectRepository;
    private final ResumeRepository resumeRepository;
    private final CertificationRepository certificationRepository;
    private final InternshipApplicationRepository internshipRepository;
    private final LearningCourseRepository learningRepository;
    private final CodingProfileRepository codingRepository;
    private final TimelineEntryRepository timelineRepository;
    private final CareerHealthService careerHealthService;

    public DashboardServiceImpl(UserProfileRepository userProfileRepository, AuditLogRepository auditLogRepository,
                                NotificationService notificationService, ProjectRepository projectRepository,
                                ResumeRepository resumeRepository, CertificationRepository certificationRepository,
                                InternshipApplicationRepository internshipRepository, LearningCourseRepository learningRepository,
                                CodingProfileRepository codingRepository, TimelineEntryRepository timelineRepository,
                                CareerHealthService careerHealthService) {
        this.userProfileRepository = userProfileRepository;
        this.auditLogRepository = auditLogRepository;
        this.notificationService = notificationService;
        this.projectRepository = projectRepository;
        this.resumeRepository = resumeRepository;
        this.certificationRepository = certificationRepository;
        this.internshipRepository = internshipRepository;
        this.learningRepository = learningRepository;
        this.codingRepository = codingRepository;
        this.timelineRepository = timelineRepository;
        this.careerHealthService = careerHealthService;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary() {
        User user = currentUser();
        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        int profileCompletion = profileCompletion(user, profile);
        com.careernest.modules.health.dto.CareerHealthResponse health = careerHealthService.getHealth();
        CareerHealthResponse dashboardHealth = new CareerHealthResponse(health.overallScore(), health.resumeScore(),
                health.skillsScore(), health.placementReadiness(), health.learningProgress(), health.codingConsistency(),
                health.projectStrength(), health.recommendations());

        return new DashboardSummaryResponse(
                new DashboardUserResponse(user.getFullName(), user.getRole().getName().name(), user.isEmailVerified(), user.isProfileSetupCompleted()),
                statistics(user, profileCompletion),
                dashboardHealth,
                learningProgress(user.getId()),
                applicationStatus(user.getId()),
                activityTrend(user.getId()),
                recentProjects(user.getId()),
                recentCertifications(user.getId()),
                List.of(),
                notifications(user, profileCompletion),
                activityFeed(user),
                quickActions(user),
                upcomingTasks(user.getId())
        );
    }

    private List<DashboardMetricResponse> statistics(User user, int profileCompletion) {
        long projectCount = projectRepository.countByUserId(user.getId());
        long resumeCount = resumeRepository.countByUserId(user.getId());
        long certificationCount = certificationRepository.countByUserId(user.getId());
        long applicationCount = internshipRepository.countByUserId(user.getId());
        long learningCount = learningRepository.countByUserId(user.getId());
        long codingProfiles = codingRepository.countByUserId(user.getId());
        long problemsSolved = codingRepository.sumProblemsSolvedByUserId(user.getId());
        long activityCount = auditLogRepository.countByUserId(user.getId());
        long unreadNotifications = notificationService.getUnreadCount();
        long learningProgress = Math.round(learningRepository.averageProgressByUserId(user.getId()));
        
        return List.of(
                new DashboardMetricResponse("profileCompletion", "Profile Completion", profileCompletion, "%", 
                    profileCompletion >= 80 ? "Strong" : "Needs attention", "blue"),
                new DashboardMetricResponse("projects", "Projects", projectCount, "", "Saved projects", "teal"),
                new DashboardMetricResponse("resumes", "Resumes", resumeCount, "", "Saved resumes", "blue"),
                new DashboardMetricResponse("certifications", "Certifications", certificationCount, "", "Saved credentials", "amber"),
                new DashboardMetricResponse("applications", "Applications", applicationCount, "", "Tracked applications", "amber"),
                new DashboardMetricResponse("learning", "Learning Progress", learningProgress, "%", learningCount + " courses", "green"),
                new DashboardMetricResponse("coding", "Problems Tracked", problemsSolved, "", codingProfiles + " coding profiles", "purple"),
                new DashboardMetricResponse("activity", "Recorded Activities", activityCount, "", "Recorded account activity", "green"),
                new DashboardMetricResponse("notifications", "Unread Notifications", unreadNotifications, "", 
                    unreadNotifications > 0 ? "You have unread messages" : "All caught up", "purple")
        );
    }

    private List<LearningProgressResponse> learningProgress(Long userId) {
        return learningRepository.findProgressSummaryByUserId(userId).stream()
                .map(row -> new LearningProgressResponse((String) row[0],
                        Math.round(((Number) row[1]).floatValue()), row[2] + " courses"))
                .toList();
    }

    private List<ChartPointResponse> applicationStatus(Long userId) {
        return internshipRepository.countStatusesByUserId(userId).stream()
                .map(row -> new ChartPointResponse((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }

    private List<ChartPointResponse> activityTrend(Long userId) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate firstDay = today.minusDays(6);
        Map<LocalDate, Long> counts = new HashMap<>();
        auditLogRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(
                        userId, firstDay.atStartOfDay().toInstant(ZoneOffset.UTC))
                .forEach(log -> counts.merge(log.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate(), 1L, Long::sum));
        return java.util.stream.LongStream.rangeClosed(0, 6)
                .mapToObj(offset -> {
                    LocalDate day = firstDay.plusDays(offset);
                    return new ChartPointResponse(day.toString().substring(5), counts.getOrDefault(day, 0L));
                })
                .toList();
    }

    private List<RecentProjectResponse> recentProjects(Long userId) {
        return projectRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 5)).getContent().stream()
                .map(project -> new RecentProjectResponse(project.getTitle(), project.getStatus(),
                        project.getTechStack(), project.getUpdatedAt().toString()))
                .toList();
    }

    private List<RecentCertificationResponse> recentCertifications(Long userId) {
        Instant now = Instant.now();
        return certificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 5)).getContent().stream()
                .map(certification -> new RecentCertificationResponse(certification.getTitle(), certification.getIssuer(),
                        certification.getExpiryDate() != null && certification.getExpiryDate().isBefore(now) ? "Expired"
                                : certification.isVerified() ? "Verified" : "Added",
                        certification.getIssueDate() == null ? null : certification.getIssueDate().toString()))
                .toList();
    }

    private List<DashboardTaskResponse> upcomingTasks(Long userId) {
        Instant now = Instant.now();
        List<DashboardTaskResponse> tasks = new ArrayList<>();
        timelineRepository.findTop5ByUserIdAndEntryDateAfterOrderByEntryDateAsc(userId, now).forEach(entry ->
                tasks.add(new DashboardTaskResponse(entry.getTitle(), entry.getEntryType(), entry.getOrganization(),
                        entry.getEntryDate(), "/timeline")));
        internshipRepository.findTop5ByUserIdAndDeadlineAfterOrderByDeadlineAsc(userId, now).forEach(application ->
                tasks.add(new DashboardTaskResponse("Application deadline: " + application.getPosition(), "APPLICATION_DEADLINE",
                        application.getCompanyName(), application.getDeadline(), "/internships")));
        return tasks.stream().sorted(Comparator.comparing(DashboardTaskResponse::dueAt)).limit(5).toList();
    }

    private List<NotificationResponse> notifications(User user, int profileCompletion) {
        List<com.careernest.modules.notification.dto.NotificationResponse> systemNotifications = 
            notificationService.getUnreadNotifications();
        
        List<NotificationResponse> items = new ArrayList<>();
        
        // Convert system notifications to dashboard format
        for (com.careernest.modules.notification.dto.NotificationResponse notif : systemNotifications) {
            items.add(new NotificationResponse(
                notif.title(),
                notif.message(),
                notif.type(),
                notif.isRead(),
                notif.createdAt().toString()
            ));
        }
        
        return items;
    }

    private List<ActivityFeedResponse> activityFeed(User user) {
        return auditLogRepository.findTop8ByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(log -> new ActivityFeedResponse(log.getEventType(), prettyTitle(log.getEventType()), log.getEventDescription(), log.getCreatedAt()))
                .toList();
    }

    private List<QuickActionResponse> quickActions(User user) {
        List<QuickActionResponse> actions = new ArrayList<>();
        actions.add(new QuickActionResponse("Complete Profile", "Improve dashboard personalization", "/profile-setup", "user"));
        actions.add(new QuickActionResponse("Create Resume", "Build an ATS-friendly resume", "/resumes", "file"));
        actions.add(new QuickActionResponse("Add Project", "Showcase practical work", "/projects/new", "folder"));
        actions.add(new QuickActionResponse("Track Internship", "Manage applications and deadlines", "/internships", "briefcase"));
        return actions;
    }

    private int profileCompletion(User user, UserProfile profile) {
        int score = 0;
        if (notBlank(user.getFullName())) score += 15;
        if (notBlank(user.getEmail()) && user.isEmailVerified()) score += 15;
        if (profile != null && notBlank(profile.getHeadline())) score += 15;
        if (profile != null && notBlank(profile.getLocation())) score += 10;
        if (profile != null && notBlank(profile.getEducationSummary())) score += 15;
        if (profile != null && notBlank(profile.getGithubUrl())) score += 10;
        if (profile != null && notBlank(profile.getLinkedinUrl())) score += 10;
        if (profile != null && notBlank(profile.getProfilePhotoUrl())) score += 10;
        return Math.min(score, 100);
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private String prettyTitle(String eventType) {
        return eventType == null ? "Activity" : eventType.replace('_', ' ').toLowerCase();
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }
}
