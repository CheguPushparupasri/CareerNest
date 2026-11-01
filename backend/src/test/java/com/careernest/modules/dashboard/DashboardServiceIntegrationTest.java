package com.careernest.modules.dashboard;

import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.certification.entity.Certification;
import com.careernest.modules.certification.repository.CertificationRepository;
import com.careernest.modules.coding.entity.CodingProfile;
import com.careernest.modules.coding.repository.CodingProfileRepository;
import com.careernest.modules.dashboard.dto.DashboardSummaryResponse;
import com.careernest.modules.dashboard.service.DashboardService;
import com.careernest.modules.internship.entity.InternshipApplication;
import com.careernest.modules.internship.repository.InternshipApplicationRepository;
import com.careernest.modules.learning.entity.LearningCourse;
import com.careernest.modules.learning.repository.LearningCourseRepository;
import com.careernest.modules.project.entity.Project;
import com.careernest.modules.project.repository.ProjectRepository;
import com.careernest.modules.timeline.entity.TimelineEntry;
import com.careernest.modules.timeline.repository.TimelineEntryRepository;
import com.careernest.modules.user.entity.AccountStatus;
import com.careernest.modules.user.entity.RoleName;
import com.careernest.modules.user.entity.User;
import com.careernest.modules.user.repository.RoleRepository;
import com.careernest.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DashboardServiceIntegrationTest {
    @Autowired private DashboardService dashboardService;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private CertificationRepository certificationRepository;
    @Autowired private LearningCourseRepository learningCourseRepository;
    @Autowired private CodingProfileRepository codingProfileRepository;
    @Autowired private InternshipApplicationRepository internshipRepository;
    @Autowired private TimelineEntryRepository timelineRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void summaryUsesPersistedModuleDataAndFutureTasksForCurrentUser() {
        User user = new User();
        user.setFullName("Dashboard Test User");
        user.setEmail("dashboard-test@example.com");
        user.setPasswordHash(passwordEncoder.encode("SecurePass@123"));
        user.setRole(roleRepository.findByName(RoleName.STUDENT).orElseThrow());
        user.setEmailVerified(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user = userRepository.save(user);

        Project project = new Project();
        project.setUser(user);
        project.setTitle("Saved project");
        project.setStatus("IN_PROGRESS");
        project.setTechStack("Java, React");
        projectRepository.save(project);

        Certification certification = new Certification();
        certification.setUser(user);
        certification.setTitle("Saved certificate");
        certification.setIssuer("CareerNest test");
        certification.setCategory("Engineering");
        certificationRepository.save(certification);

        LearningCourse course = new LearningCourse();
        course.setUser(user);
        course.setTitle("Spring course");
        course.setCategory("Backend");
        course.setStatus("IN_PROGRESS");
        course.setProgress(55);
        learningCourseRepository.save(course);

        CodingProfile codingProfile = new CodingProfile();
        codingProfile.setUser(user);
        codingProfile.setPlatform("LeetCode");
        codingProfile.setUsername("dashboard-user");
        codingProfile.setProblemsSolved(23);
        codingProfileRepository.save(codingProfile);

        InternshipApplication application = new InternshipApplication();
        application.setUser(user);
        application.setCompanyName("Example Company");
        application.setPosition("Intern");
        application.setStatus("APPLIED");
        application.setDeadline(Instant.now().plusSeconds(86_400));
        internshipRepository.save(application);

        TimelineEntry milestone = new TimelineEntry();
        milestone.setUser(user);
        milestone.setEntryType("ACADEMIC");
        milestone.setTitle("Future milestone");
        milestone.setEntryDate(Instant.now().plusSeconds(172_800));
        timelineRepository.save(milestone);

        CustomUserDetails principal = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()));

        DashboardSummaryResponse summary = dashboardService.summary();
        Map<String, Long> metrics = summary.statistics().stream()
                .collect(Collectors.toMap(item -> item.key(), item -> item.value()));

        assertThat(metrics).containsEntry("projects", 1L)
                .containsEntry("certifications", 1L)
                .containsEntry("applications", 1L)
                .containsEntry("coding", 23L);
        assertThat(summary.learningProgress()).singleElement()
                .satisfies(item -> {
                    assertThat(item.track()).isEqualTo("Backend");
                    assertThat(item.progress()).isEqualTo(55);
                });
        assertThat(summary.applicationStatus().stream().map(point -> point.label()).toList()).contains("APPLIED");
        assertThat(summary.recentProjects()).extracting("title").contains("Saved project");
        assertThat(summary.recentCertifications()).extracting("title").contains("Saved certificate");
        assertThat(summary.upcomingTasks()).extracting("title").contains("Future milestone", "Application deadline: Intern");
    }
}