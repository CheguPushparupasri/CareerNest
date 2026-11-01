package com.careernest.modules.profile.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.profile.dto.ProfileSetupRequest;
import com.careernest.modules.profile.dto.ProfileSetupResponse;
import com.careernest.modules.profile.service.ProfilePhotoService;
import com.careernest.modules.profile.service.ProfileSetupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile Setup")
public class ProfileSetupController {
    private final ProfileSetupService profileSetupService;
    private final ProfilePhotoService profilePhotoService;

    public ProfileSetupController(ProfileSetupService profileSetupService, ProfilePhotoService profilePhotoService) {
        this.profileSetupService = profileSetupService;
        this.profilePhotoService = profilePhotoService;
    }

    @GetMapping("/setup")
    public ApiResponse<ProfileSetupResponse> get() {
        return ApiResponse.success("Profile setup fetched", profileSetupService.get());
    }

    @PutMapping("/setup")
    public ApiResponse<ProfileSetupResponse> update(@Valid @RequestBody ProfileSetupRequest request) {
        return ApiResponse.success("Profile setup updated", profileSetupService.update(request));
    }

    @PostMapping("/photo")
    public ApiResponse<ProfileSetupResponse> uploadPhoto(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success("Profile photo uploaded", profilePhotoService.upload(file));
    }

    @DeleteMapping("/photo")
    public ApiResponse<Void> deletePhoto() {
        profilePhotoService.delete();
        return ApiResponse.success("Profile photo removed");
    }
}
