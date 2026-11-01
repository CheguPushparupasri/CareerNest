package com.careernest.modules.profile.service;

import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.profile.dto.ProfileSetupRequest;
import com.careernest.modules.profile.dto.ProfileSetupResponse;
import com.careernest.modules.profile.entity.UserProfile;
import com.careernest.modules.profile.repository.UserProfileRepository;
import com.careernest.modules.user.entity.User;
import com.careernest.modules.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileSetupService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public ProfileSetupService(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    public ProfileSetupResponse get() {
        return toResponse(profile());
    }

    @Transactional
    public ProfileSetupResponse update(ProfileSetupRequest request) {
        UserProfile profile = profile();
        profile.setHeadline(request.headline());
        profile.setPhone(request.phone());
        profile.setLocation(request.location());
        profile.setEducationSummary(request.educationSummary());
        profile.setPortfolioUrl(request.portfolioUrl());
        profile.setLinkedinUrl(request.linkedinUrl());
        profile.setGithubUrl(request.githubUrl());
        User user = profile.getUser();
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }
        user.setProfileSetupCompleted(isComplete(profile));
        userRepository.save(user);
        return toResponse(profile);
    }

    private boolean isComplete(UserProfile profile) {
        return notBlank(profile.getHeadline()) && notBlank(profile.getLocation()) && notBlank(profile.getEducationSummary());
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private UserProfile profile() {
        return userProfileRepository.findByUserId(currentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }

    private ProfileSetupResponse toResponse(UserProfile profile) {
        return new ProfileSetupResponse(profile.getUser().getFullName(), profile.getHeadline(), profile.getPhone(), profile.getLocation(),
                profile.getEducationSummary(), profile.getPortfolioUrl(), profile.getLinkedinUrl(),
                profile.getGithubUrl(), profile.getProfilePhotoUrl(), profile.getUser().isProfileSetupCompleted());
    }
}
