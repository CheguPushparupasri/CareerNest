package com.careernest.modules.project.service;

import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.project.dto.ProjectListResponse;
import com.careernest.modules.project.dto.ProjectRequest;
import com.careernest.modules.project.dto.ProjectResponse;
import com.careernest.modules.project.dto.ScreenshotResponse;
import com.careernest.modules.project.entity.Project;
import com.careernest.modules.project.entity.ProjectScreenshot;
import com.careernest.modules.project.repository.ProjectRepository;
import com.careernest.modules.project.repository.ProjectScreenshotRepository;
import com.careernest.modules.notification.service.NotificationService;
import com.careernest.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectScreenshotRepository screenshotRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public ProjectServiceImpl(ProjectRepository projectRepository, 
                            ProjectScreenshotRepository screenshotRepository,
                            AuditLogService auditLogService,
                            NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.screenshotRepository = screenshotRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project();
        project.setUser(currentUser());
        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setGithubUrl(request.githubUrl());
        project.setLiveDemoUrl(request.liveDemoUrl());
        project.setTechStack(request.techStack());
        project.setStatus(request.status() != null ? request.status() : "IN_PROGRESS");
        project.setFeatured(request.featured());
        
        if (request.startDate() != null) {
            project.setStartDate(Instant.parse(request.startDate()));
        }
        if (request.endDate() != null) {
            project.setEndDate(Instant.parse(request.endDate()));
        }
        
        Project saved = projectRepository.save(project);
        auditLogService.record(currentUser(), currentUser(), "PROJECT_CREATED", 
            "Created project: " + saved.getTitle(), null, null);
        notificationService.createNotification("Project added", saved.getTitle() + " was added to your portfolio.",
            "INFO", "/projects/" + saved.getId());
        
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getProjectByIdAndUser(id);
        
        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setGithubUrl(request.githubUrl());
        project.setLiveDemoUrl(request.liveDemoUrl());
        project.setTechStack(request.techStack());
        project.setStatus(request.status());
        project.setFeatured(request.featured());
        
        if (request.startDate() != null) {
            project.setStartDate(Instant.parse(request.startDate()));
        }
        if (request.endDate() != null) {
            project.setEndDate(Instant.parse(request.endDate()));
        }
        
        Project saved = projectRepository.save(project);
        auditLogService.record(currentUser(), currentUser(), "PROJECT_UPDATED", 
            "Updated project: " + saved.getTitle(), null, null);
        
        return toResponse(saved);
    }

    @Override
    public ProjectResponse getById(Long id) {
        return toResponse(getProjectByIdAndUser(id));
    }

    @Override
    public Page<ProjectListResponse> getAll(Pageable pageable) {
        return projectRepository.findByUserIdOrderByCreatedAtDesc(currentUser().getId(), pageable)
                .map(this::toListResponse);
    }

    @Override
    public Page<ProjectListResponse> search(String query, Pageable pageable) {
        return projectRepository.searchProjects(currentUser().getId(), query, pageable)
                .map(this::toListResponse);
    }

    @Override
    public Page<ProjectListResponse> getByStatus(String status, Pageable pageable) {
        return projectRepository.findByUserIdAndStatus(currentUser().getId(), status, pageable)
                .map(this::toListResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = getProjectByIdAndUser(id);
        screenshotRepository.deleteByProjectId(id);
        projectRepository.delete(project);
        auditLogService.record(currentUser(), currentUser(), "PROJECT_DELETED", 
            "Deleted project: " + project.getTitle(), null, null);
    }

    @Override
    @Transactional
    public void toggleFeatured(Long id) {
        Project project = getProjectByIdAndUser(id);
        project.setFeatured(!project.isFeatured());
        projectRepository.save(project);
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

    private ProjectResponse toResponse(Project project) {
        List<ScreenshotResponse> screenshots = screenshotRepository
                .findByProjectIdOrderByDisplayOrderAsc(project.getId())
                .stream()
                .map(s -> new ScreenshotResponse(s.getId(), s.getImageUrl(), s.getDisplayOrder()))
                .toList();
        
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getGithubUrl(),
                project.getLiveDemoUrl(),
                project.getTechStack(),
                project.getStatus(),
                project.getStartDate(),
                project.getEndDate(),
                project.isFeatured(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                screenshots
        );
    }

    private ProjectListResponse toListResponse(Project project) {
        int screenshotCount = screenshotRepository.findByProjectIdOrderByDisplayOrderAsc(project.getId()).size();
        return new ProjectListResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getGithubUrl(),
                project.getLiveDemoUrl(),
                project.getTechStack(),
                project.getStatus(),
                project.isFeatured(),
                project.getCreatedAt(),
                screenshotCount
        );
    }
}
