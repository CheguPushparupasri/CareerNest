package com.careernest.modules.project.service;

import com.careernest.common.exception.BadRequestException;
import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ServiceUnavailableException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.project.dto.ScreenshotResponse;
import com.careernest.modules.project.entity.Project;
import com.careernest.modules.project.entity.ProjectScreenshot;
import com.careernest.modules.project.repository.ProjectRepository;
import com.careernest.modules.project.repository.ProjectScreenshotRepository;
import com.careernest.modules.user.entity.User;
import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ProjectScreenshotServiceImpl implements ProjectScreenshotService {
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    
    private final ProjectScreenshotRepository screenshotRepository;
    private final ProjectRepository projectRepository;
    private final ObjectProvider<Cloudinary> cloudinaryProvider;
    private final String folder;

    public ProjectScreenshotServiceImpl(ProjectScreenshotRepository screenshotRepository,
                                      ProjectRepository projectRepository,
                                      ObjectProvider<Cloudinary> cloudinaryProvider,
                                      @Value("${app.cloudinary.folder}") String folder) {
        this.screenshotRepository = screenshotRepository;
        this.projectRepository = projectRepository;
        this.cloudinaryProvider = cloudinaryProvider;
        this.folder = folder;
    }

    @Override
    @Transactional
    public ScreenshotResponse upload(Long projectId, MultipartFile file) {
        if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("Screenshot must be under 5 MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only JPEG, PNG, and WebP images are allowed");
        }
        Cloudinary cloudinary = cloudinaryProvider.getIfAvailable();
        if (cloudinary == null) {
            throw new ServiceUnavailableException("Project screenshot storage is not configured");
        }

        Project project = getProjectByIdAndUser(projectId);
        
        try {
            int currentOrder = screenshotRepository.findByProjectIdOrderByDisplayOrderAsc(projectId).size();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "folder", folder + "/projects/" + project.getId(),
                    "resource_type", "image"
            ));
            
            ProjectScreenshot screenshot = new ProjectScreenshot();
            screenshot.setProject(project);
            screenshot.setImageUrl((String) result.get("secure_url"));
            screenshot.setPublicId((String) result.get("public_id"));
            screenshot.setDisplayOrder(currentOrder);
            
            ProjectScreenshot saved = screenshotRepository.save(screenshot);
            return new ScreenshotResponse(saved.getId(), saved.getImageUrl(), saved.getDisplayOrder());
        } catch (IOException ex) {
            throw new BadRequestException("Unable to upload screenshot");
        }
    }

    @Override
    @Transactional
    public void delete(Long screenshotId) {
        ProjectScreenshot screenshot = screenshotRepository.findById(screenshotId)
                .orElseThrow(() -> new ResourceNotFoundException("Screenshot not found"));
        
        // Verify ownership
        if (!screenshot.getProject().getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        
        // Delete from Cloudinary
        try {
            if (screenshot.getPublicId() != null) {
                Cloudinary cloudinary = cloudinaryProvider.getIfAvailable();
                if (cloudinary != null) {
                    cloudinary.uploader().destroy(screenshot.getPublicId(), Map.of("resource_type", "image"));
                }
            }
        } catch (IOException ignored) {
        }
        
        screenshotRepository.delete(screenshot);
    }

    @Override
    @Transactional
    public void reorder(Long projectId, List<Long> screenshotIds) {
        Project project = getProjectByIdAndUser(projectId);
        
        List<ProjectScreenshot> screenshots = screenshotRepository.findByProjectIdOrderByDisplayOrderAsc(projectId);
        
        for (int i = 0; i < screenshotIds.size(); i++) {
            Long screenshotId = screenshotIds.get(i);
            ProjectScreenshot screenshot = screenshots.stream()
                    .filter(s -> s.getId().equals(screenshotId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Screenshot not found"));
            screenshot.setDisplayOrder(i);
        }
        
        screenshotRepository.saveAll(screenshots);
    }

    private Project getProjectByIdAndUser(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        return project;
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }
}
