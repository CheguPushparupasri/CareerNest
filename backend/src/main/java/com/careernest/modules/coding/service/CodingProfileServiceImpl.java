package com.careernest.modules.coding.service;

import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.coding.dto.CodingProfileRequest;
import com.careernest.modules.coding.dto.CodingProfileResponse;
import com.careernest.modules.coding.entity.CodingProfile;
import com.careernest.modules.coding.repository.CodingProfileRepository;
import com.careernest.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CodingProfileServiceImpl implements CodingProfileService {
    private final CodingProfileRepository repository;
    private final AuditLogService auditLogService;

    public CodingProfileServiceImpl(CodingProfileRepository repository, AuditLogService auditLogService) {
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public CodingProfileResponse create(CodingProfileRequest request) {
        CodingProfile profile = new CodingProfile();
        profile.setUser(currentUser());
        profile.setPlatform(request.platform());
        profile.setUsername(request.username());
        profile.setProfileUrl(request.profileUrl());
        profile.setRating(request.rating());
        profile.setProblemsSolved(request.problemsSolved());
        profile.setStreak(request.streak());
        profile.setBadgeCount(request.badgeCount());

        CodingProfile saved = repository.save(profile);
        auditLogService.record(currentUser(), currentUser(), "CODING_PROFILE_CREATED",
                "Created coding profile: " + saved.getPlatform(), null, null);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public CodingProfileResponse update(Long id, CodingProfileRequest request) {
        CodingProfile profile = getByIdAndUser(id);
        profile.setPlatform(request.platform());
        profile.setUsername(request.username());
        profile.setProfileUrl(request.profileUrl());
        profile.setRating(request.rating());
        profile.setProblemsSolved(request.problemsSolved());
        profile.setStreak(request.streak());
        profile.setBadgeCount(request.badgeCount());

        CodingProfile saved = repository.save(profile);
        auditLogService.record(currentUser(), currentUser(), "CODING_PROFILE_UPDATED",
                "Updated coding profile: " + saved.getPlatform(), null, null);
        return toResponse(saved);
    }

    @Override
    public CodingProfileResponse getById(Long id) {
        return toResponse(getByIdAndUser(id));
    }

    @Override
    public Page<CodingProfileResponse> getAll(Pageable pageable) {
        return repository.findByUserIdOrderByCreatedAtDesc(currentUser().getId(), pageable).map(this::toResponse);
    }

    @Override
    public Page<CodingProfileResponse> search(String query, Pageable pageable) {
        return repository.searchProfiles(currentUser().getId(), query, pageable).map(this::toResponse);
    }

    @Override
    public Page<CodingProfileResponse> getByPlatform(String platform, Pageable pageable) {
        return repository.findByUserIdAndPlatform(currentUser().getId(), platform, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CodingProfile profile = getByIdAndUser(id);
        repository.delete(profile);
        auditLogService.record(currentUser(), currentUser(), "CODING_PROFILE_DELETED",
                "Deleted coding profile: " + profile.getPlatform(), null, null);
    }

    private CodingProfile getByIdAndUser(Long id) {
        CodingProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coding profile not found"));
        if (!profile.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        return profile;
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }

    private CodingProfileResponse toResponse(CodingProfile profile) {
        return new CodingProfileResponse(
                profile.getId(),
                profile.getPlatform(),
                profile.getUsername(),
                profile.getProfileUrl(),
                profile.getRating(),
                profile.getProblemsSolved(),
                profile.getStreak(),
                profile.getBadgeCount(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
