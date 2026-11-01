package com.careernest.modules.certification.service;

import com.careernest.common.exception.BadRequestException;
import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.certification.dto.CertificationRequest;
import com.careernest.modules.certification.dto.CertificationResponse;
import com.careernest.modules.certification.entity.Certification;
import com.careernest.modules.certification.repository.CertificationRepository;
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

@Service
public class CertificationServiceImpl implements CertificationService {
    private final CertificationRepository certificationRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public CertificationServiceImpl(CertificationRepository certificationRepository, AuditLogService auditLogService,
                                    NotificationService notificationService) {
        this.certificationRepository = certificationRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public CertificationResponse create(CertificationRequest request) {
        Certification certification = new Certification();
        certification.setUser(currentUser());
        certification.setTitle(request.title());
        certification.setIssuer(request.issuer());
        certification.setCategory(request.category());
        certification.setDescription(request.description());
        certification.setCredentialUrl(request.credentialUrl());
        certification.setImageUrl(request.imageUrl());
        certification.setVerified(request.verified());
        certification.setIssueDate(parseInstant(request.issueDate()));
        certification.setExpiryDate(parseInstant(request.expiryDate()));

        Certification saved = certificationRepository.save(certification);
        auditLogService.record(currentUser(), currentUser(), "CERTIFICATION_CREATED",
                "Created certification: " + saved.getTitle(), null, null);
        notificationService.createNotification("Certification added", saved.getTitle() + " was added to your credentials.",
            "INFO", "/certifications");
        return toResponse(saved);
    }

    @Override
    @Transactional
    public CertificationResponse update(Long id, CertificationRequest request) {
        Certification certification = getCertificationByIdAndUser(id);
        certification.setTitle(request.title());
        certification.setIssuer(request.issuer());
        certification.setCategory(request.category());
        certification.setDescription(request.description());
        certification.setCredentialUrl(request.credentialUrl());
        certification.setImageUrl(request.imageUrl());
        certification.setVerified(request.verified());
        certification.setIssueDate(parseInstant(request.issueDate()));
        certification.setExpiryDate(parseInstant(request.expiryDate()));

        Certification saved = certificationRepository.save(certification);
        auditLogService.record(currentUser(), currentUser(), "CERTIFICATION_UPDATED",
                "Updated certification: " + saved.getTitle(), null, null);
        return toResponse(saved);
    }

    @Override
    public CertificationResponse getById(Long id) {
        return toResponse(getCertificationByIdAndUser(id));
    }

    @Override
    public Page<CertificationResponse> getAll(Pageable pageable) {
        return certificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser().getId(), pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<CertificationResponse> search(String query, Pageable pageable) {
        return certificationRepository.searchCertifications(currentUser().getId(), query, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<CertificationResponse> getByCategory(String category, Pageable pageable) {
        return certificationRepository.findByUserIdAndCategory(currentUser().getId(), category, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Certification certification = getCertificationByIdAndUser(id);
        certificationRepository.delete(certification);
        auditLogService.record(currentUser(), currentUser(), "CERTIFICATION_DELETED",
                "Deleted certification: " + certification.getTitle(), null, null);
    }

    @Override
    public long count() {
        return certificationRepository.countByUserId(currentUser().getId());
    }

    private Certification getCertificationByIdAndUser(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));
        if (!certification.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        return certification;
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

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }

    private CertificationResponse toResponse(Certification certification) {
        return new CertificationResponse(
                certification.getId(),
                certification.getTitle(),
                certification.getIssuer(),
                certification.getCategory(),
                certification.getDescription(),
                certification.getCredentialUrl(),
                certification.getImageUrl(),
                certification.getIssueDate(),
                certification.getExpiryDate(),
                certification.isVerified(),
                certification.getCreatedAt(),
                certification.getUpdatedAt()
        );
    }
}
