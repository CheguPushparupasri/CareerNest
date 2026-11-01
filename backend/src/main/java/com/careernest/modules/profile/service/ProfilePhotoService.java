package com.careernest.modules.profile.service;

import com.careernest.common.exception.BadRequestException;
import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ServiceUnavailableException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.profile.dto.ProfileSetupResponse;
import com.careernest.modules.profile.entity.UserProfile;
import com.careernest.modules.profile.repository.UserProfileRepository;
import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
public class ProfilePhotoService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final UserProfileRepository userProfileRepository;
    private final ObjectProvider<Cloudinary> cloudinaryProvider;
    private final String folder;

    public ProfilePhotoService(UserProfileRepository userProfileRepository, ObjectProvider<Cloudinary> cloudinaryProvider,
                               @Value("${app.cloudinary.folder}") String folder) {
        this.userProfileRepository = userProfileRepository;
        this.cloudinaryProvider = cloudinaryProvider;
        this.folder = folder;
    }

    @Transactional
    public ProfileSetupResponse upload(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > 3 * 1024 * 1024) {
            throw new BadRequestException("Profile photo must be under 3 MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only JPEG, PNG, and WebP images are allowed");
        }
        Cloudinary cloudinary = cloudinaryProvider.getIfAvailable();
        if (cloudinary == null) {
            throw new ServiceUnavailableException("Profile photo storage is not configured");
        }
        UserProfile profile = profile();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "folder", folder + "/profile-photos/" + profile.getUser().getPublicId(),
                    "resource_type", "image"
            ));
            deleteExisting(profile);
            profile.setProfilePhotoUrl((String) result.get("secure_url"));
            profile.setProfilePhotoPublicId((String) result.get("public_id"));
            return response(profile);
        } catch (IOException ex) {
            throw new BadRequestException("Unable to upload profile photo");
        }
    }

    @Transactional
    public void delete() {
        UserProfile profile = profile();
        deleteExisting(profile);
        profile.setProfilePhotoUrl(null);
        profile.setProfilePhotoPublicId(null);
    }

    private void deleteExisting(UserProfile profile) {
        if (profile.getProfilePhotoPublicId() == null) {
            return;
        }
        Cloudinary cloudinary = cloudinaryProvider.getIfAvailable();
        if (cloudinary == null) {
            return;
        }
        try {
            cloudinary.uploader().destroy(profile.getProfilePhotoPublicId(), Map.of("resource_type", "image"));
        } catch (IOException ignored) {
        }
    }

    private UserProfile profile() {
        return userProfileRepository.findByUserId(currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getId();
        }
        throw new UnauthorizedException("Authentication is required");
    }

    private ProfileSetupResponse response(UserProfile profile) {
        return new ProfileSetupResponse(profile.getUser().getFullName(), profile.getHeadline(), profile.getPhone(), profile.getLocation(),
                profile.getEducationSummary(), profile.getPortfolioUrl(), profile.getLinkedinUrl(),
                profile.getGithubUrl(), profile.getProfilePhotoUrl(), profile.getUser().isProfileSetupCompleted());
    }
}
