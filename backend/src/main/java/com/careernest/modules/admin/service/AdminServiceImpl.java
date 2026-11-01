package com.careernest.modules.admin.service;

import com.careernest.modules.admin.dto.AdminAuditLogResponse;
import com.careernest.modules.admin.dto.AdminOverviewResponse;
import com.careernest.modules.admin.dto.AdminUserResponse;
import com.careernest.modules.audit.entity.AuditLog;
import com.careernest.modules.audit.repository.AuditLogRepository;
import com.careernest.modules.certification.repository.CertificationRepository;
import com.careernest.modules.coding.repository.CodingProfileRepository;
import com.careernest.modules.internship.repository.InternshipApplicationRepository;
import com.careernest.modules.learning.repository.LearningCourseRepository;
import com.careernest.modules.project.repository.ProjectRepository;
import com.careernest.modules.resume.repository.ResumeRepository;
import com.careernest.modules.user.entity.User;
import com.careernest.modules.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ResumeRepository resumeRepository;
    private final CertificationRepository certificationRepository;
    private final InternshipApplicationRepository internshipApplicationRepository;
    private final LearningCourseRepository learningCourseRepository;
    private final CodingProfileRepository codingProfileRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminServiceImpl(UserRepository userRepository, ProjectRepository projectRepository, ResumeRepository resumeRepository,
                            CertificationRepository certificationRepository, InternshipApplicationRepository internshipApplicationRepository,
                            LearningCourseRepository learningCourseRepository, CodingProfileRepository codingProfileRepository,
                            AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.resumeRepository = resumeRepository;
        this.certificationRepository = certificationRepository;
        this.internshipApplicationRepository = internshipApplicationRepository;
        this.learningCourseRepository = learningCourseRepository;
        this.codingProfileRepository = codingProfileRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AdminOverviewResponse overview() {
        return new AdminOverviewResponse(
                userRepository.count(),
                userRepository.countByDeletedAtIsNull(),
                projectRepository.count(),
                resumeRepository.count(),
                certificationRepository.count(),
                internshipApplicationRepository.count(),
                learningCourseRepository.count(),
                codingProfileRepository.count()
        );
    }

    @Override
    public Page<AdminUserResponse> getUsers(String query, Pageable pageable) {
        Page<User> users = query == null || query.isBlank()
                ? userRepository.findByDeletedAtIsNull(pageable)
                : userRepository.searchActiveUsers(query, pageable);
        return users.map(this::toUserResponse);
    }

    @Override
    public Page<AdminAuditLogResponse> getAuditLogs(String query, Pageable pageable) {
        Page<AuditLog> logs = query == null || query.isBlank()
                ? auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                : auditLogRepository.searchLogs(query, pageable);
        return logs.map(this::toAuditResponse);
    }

    private AdminUserResponse toUserResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getPublicId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName().name(),
                user.getAccountStatus().name(),
                user.isEmailVerified(),
                user.isProfileSetupCompleted(),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }

    private AdminAuditLogResponse toAuditResponse(AuditLog log) {
        return new AdminAuditLogResponse(
                log.getId(),
                log.getUser() != null ? log.getUser().getEmail() : null,
                log.getActor() != null ? log.getActor().getEmail() : null,
                log.getEventType(),
                log.getEventDescription(),
                log.getIpAddress(),
                log.getCreatedAt()
        );
    }
}
