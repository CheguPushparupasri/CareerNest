package com.careernest.modules.internship.service;

import com.careernest.common.exception.BadRequestException;
import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.internship.dto.InternshipApplicationRequest;
import com.careernest.modules.internship.dto.InternshipApplicationResponse;
import com.careernest.modules.internship.entity.InternshipApplication;
import com.careernest.modules.internship.repository.InternshipApplicationRepository;
import com.careernest.modules.notification.service.NotificationService;
import com.careernest.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Set;

@Service
public class InternshipApplicationServiceImpl implements InternshipApplicationService {
    private final InternshipApplicationRepository repository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private static final Set<String> STATUSES = Set.of(
            "SAVED", "APPLIED", "ASSESSMENT", "SCREENING", "INTERVIEW", "OFFER", "SELECTED", "REJECTED");

    public InternshipApplicationServiceImpl(InternshipApplicationRepository repository, AuditLogService auditLogService,
                                            NotificationService notificationService) {
        this.repository = repository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public InternshipApplicationResponse create(InternshipApplicationRequest request) {
        InternshipApplication application = new InternshipApplication();
        application.setUser(currentUser());
        application.setCompanyName(request.companyName());
        application.setPosition(request.position());
        application.setRoleType(request.roleType());
        application.setLocation(request.location());
        application.setStatus(validatedStatus(request.status() == null ? "APPLIED" : request.status()));
        application.setSource(request.source());
        application.setNotes(request.notes());
        application.setApplicationDate(parseInstant(request.applicationDate()));
        application.setDeadline(parseInstant(request.deadline()));
        application.setInterviewStage(request.interviewStage() != null ? request.interviewStage() : "APPLIED");

        InternshipApplication saved = repository.save(application);
        auditLogService.record(currentUser(), currentUser(), "INTERNSHIP_CREATED",
                "Created application: " + saved.getCompanyName(), null, null);
        notificationService.createNotification("Application tracked", saved.getPosition() + " at " + saved.getCompanyName()
            + " was added.", "INFO", "/internships");
        return toResponse(saved);
    }

    @Override
    @Transactional
    public InternshipApplicationResponse update(Long id, InternshipApplicationRequest request) {
        InternshipApplication application = getByIdAndUser(id);
        application.setCompanyName(request.companyName());
        application.setPosition(request.position());
        application.setRoleType(request.roleType());
        application.setLocation(request.location());
        application.setStatus(validatedStatus(request.status() == null ? application.getStatus() : request.status()));
        application.setSource(request.source());
        application.setNotes(request.notes());
        application.setApplicationDate(parseInstant(request.applicationDate()));
        application.setDeadline(parseInstant(request.deadline()));
        application.setInterviewStage(request.interviewStage());

        InternshipApplication saved = repository.save(application);
        auditLogService.record(currentUser(), currentUser(), "INTERNSHIP_UPDATED",
                "Updated application: " + saved.getCompanyName(), null, null);
        return toResponse(saved);
    }

    @Override
    public InternshipApplicationResponse getById(Long id) {
        return toResponse(getByIdAndUser(id));
    }

    @Override
    public Page<InternshipApplicationResponse> getAll(Pageable pageable) {
        return repository.findByUserIdOrderByCreatedAtDesc(currentUser().getId(), pageable).map(this::toResponse);
    }

    @Override
    public Page<InternshipApplicationResponse> search(String query, Pageable pageable) {
        return repository.searchApplications(currentUser().getId(), query, pageable).map(this::toResponse);
    }

    @Override
    public Page<InternshipApplicationResponse> getByStatus(String status, Pageable pageable) {
        return repository.findByUserIdAndStatus(currentUser().getId(), status, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        InternshipApplication application = getByIdAndUser(id);
        repository.delete(application);
        auditLogService.record(currentUser(), currentUser(), "INTERNSHIP_DELETED",
                "Deleted application: " + application.getCompanyName(), null, null);
    }

    @Override
    public long count() {
        return repository.countByUserId(currentUser().getId());
    }

    private InternshipApplication getByIdAndUser(Long id) {
        InternshipApplication application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship application not found"));
        if (!application.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        return application;
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (Exception instantError) {
            try {
                return LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC).toInstant();
            } catch (Exception dateError) {
                throw new BadRequestException("Invalid date format. Use ISO-8601 or YYYY-MM-DD.");
            }
        }
    }

    private String validatedStatus(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!STATUSES.contains(normalized)) {
            throw new BadRequestException("Unsupported application status");
        }
        return normalized;
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }

    private InternshipApplicationResponse toResponse(InternshipApplication application) {
        return new InternshipApplicationResponse(
                application.getId(),
                application.getCompanyName(),
                application.getPosition(),
                application.getRoleType(),
                application.getLocation(),
                application.getStatus(),
                application.getSource(),
                application.getNotes(),
                application.getApplicationDate(),
                application.getDeadline(),
                application.getInterviewStage(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
