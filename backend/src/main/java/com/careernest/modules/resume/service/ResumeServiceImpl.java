package com.careernest.modules.resume.service;

import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.resume.dto.ResumeRequest;
import com.careernest.modules.resume.dto.ResumeResponse;
import com.careernest.modules.resume.dto.ResumeVersionResponse;
import com.careernest.modules.resume.entity.Resume;
import com.careernest.modules.resume.entity.ResumeVersion;
import com.careernest.modules.resume.repository.ResumeRepository;
import com.careernest.modules.resume.repository.ResumeVersionRepository;
import com.careernest.modules.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {
    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository versionRepository;
    private final AuditLogService auditLogService;

    public ResumeServiceImpl(ResumeRepository resumeRepository,
                            ResumeVersionRepository versionRepository,
                            AuditLogService auditLogService) {
        this.resumeRepository = resumeRepository;
        this.versionRepository = versionRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public ResumeResponse create(ResumeRequest request) {
        // If setting as primary, unset other primary resumes
        if (request.isPrimary()) {
            resumeRepository.findByUserIdAndIsPrimaryTrue(currentUser().getId())
                    .ifPresent(r -> r.setPrimary(false));
        }

        Resume resume = new Resume();
        resume.setUser(currentUser());
        resume.setTitle(request.title());
        resume.setTemplate(request.template());
        resume.setContent(request.content());
        resume.setPrimary(request.isPrimary());
        resume.setAtsScore(calculateAtsScore(request.content()));

        Resume saved = resumeRepository.save(resume);
        auditLogService.record(currentUser(), currentUser(), "RESUME_CREATED",
            "Created resume: " + saved.getTitle(), null, null);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public ResumeResponse update(Long id, ResumeRequest request) {
        Resume resume = getResumeByIdAndUser(id);

        // Save version before updating
        saveVersion(resume, "Updated resume content");

        resume.setTitle(request.title());
        resume.setTemplate(request.template());
        resume.setContent(request.content());
        
        if (request.isPrimary()) {
            resumeRepository.findByUserIdAndIsPrimaryTrue(currentUser().getId())
                    .ifPresent(r -> {
                        if (!r.getId().equals(id)) r.setPrimary(false);
                    });
            resume.setPrimary(true);
        }

        resume.setAtsScore(calculateAtsScore(request.content()));

        Resume saved = resumeRepository.save(resume);
        auditLogService.record(currentUser(), currentUser(), "RESUME_UPDATED",
            "Updated resume: " + saved.getTitle(), null, null);

        return toResponse(saved);
    }

    @Override
    public ResumeResponse getById(Long id) {
        return toResponse(getResumeByIdAndUser(id));
    }

    @Override
    public List<ResumeResponse> getAll() {
        return resumeRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(currentUser().getId()))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Resume resume = getResumeByIdAndUser(id);
        versionRepository.deleteByResumeId(id);
        resumeRepository.delete(resume);
        auditLogService.record(currentUser(), currentUser(), "RESUME_DELETED",
            "Deleted resume: " + resume.getTitle(), null, null);
    }

    @Override
    @Transactional
    public void setPrimary(Long id) {
        Resume resume = getResumeByIdAndUser(id);
        resumeRepository.findByUserIdAndIsPrimaryTrue(currentUser().getId())
                .ifPresent(r -> {
                    if (!r.getId().equals(id)) r.setPrimary(false);
                });
        resume.setPrimary(true);
        resumeRepository.save(resume);
    }

    @Override
    public ResumeResponse getPrimary() {
        return resumeRepository.findByUserIdAndIsPrimaryTrue(currentUser().getId())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No primary resume found"));
    }

    @Override
    public List<ResumeVersionResponse> getVersions(Long resumeId) {
        getResumeByIdAndUser(resumeId);
        return versionRepository.findByResumeIdOrderByCreatedAtDesc(resumeId)
                .stream()
                .map(this::toVersionResponse)
                .toList();
    }

    @Override
    public ResumeResponse exportToPdf(Long id) {
        Resume resume = getResumeByIdAndUser(id);
        // PDF export functionality would be implemented here
        // For now, return the resume data
        return toResponse(resume);
    }

    private Resume getResumeByIdAndUser(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        if (!resume.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        return resume;
    }

    private void saveVersion(Resume resume, String description) {
        int currentVersion = versionRepository.findByResumeIdOrderByCreatedAtDesc(resume.getId())
                .stream()
                .mapToInt(ResumeVersion::getVersionNumber)
                .max()
                .orElse(0);

        ResumeVersion version = new ResumeVersion();
        version.setResume(resume);
        version.setVersionNumber(currentVersion + 1);
        version.setContent(resume.getContent());
        version.setChangeDescription(description);
        versionRepository.save(version);
    }

    private Integer calculateAtsScore(String content) {
        if (content == null || content.isEmpty()) return 0;
        
        int score = 50; // Base score
        String lowerContent = content.toLowerCase();
        
        // Check for common ATS keywords
        String[] keywords = {"experience", "education", "skills", "projects", "achievements", 
                           "leadership", "communication", "teamwork", "problem solving"};
        
        for (String keyword : keywords) {
            if (lowerContent.contains(keyword)) {
                score += 5;
            }
        }
        
        return Math.min(score, 100);
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }

    private ResumeResponse toResponse(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getTitle(),
                resume.getTemplate(),
                resume.getContent(),
                resume.isPrimary(),
                resume.getAtsScore(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }

    private ResumeVersionResponse toVersionResponse(ResumeVersion version) {
        return new ResumeVersionResponse(
                version.getId(),
                version.getVersionNumber(),
                version.getContent(),
                version.getChangeDescription(),
                version.getCreatedAt()
        );
    }
}
