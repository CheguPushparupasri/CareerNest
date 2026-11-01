package com.careernest.modules.learning.service;

import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.BadRequestException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.learning.dto.LearningCourseRequest;
import com.careernest.modules.learning.dto.LearningCourseResponse;
import com.careernest.modules.learning.entity.LearningCourse;
import com.careernest.modules.learning.repository.LearningCourseRepository;
import com.careernest.modules.notification.service.NotificationService;
import com.careernest.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class LearningCourseServiceImpl implements LearningCourseService {
    private final LearningCourseRepository repository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    private static final Set<String> STATUSES = Set.of("PLANNED", "IN_PROGRESS", "COMPLETED");

    public LearningCourseServiceImpl(LearningCourseRepository repository, AuditLogService auditLogService,
                                     NotificationService notificationService) {
        this.repository = repository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public LearningCourseResponse create(LearningCourseRequest request) {
        LearningCourse course = new LearningCourse();
        course.setUser(currentUser());
        course.setTitle(request.title());
        course.setProvider(request.provider());
        course.setCategory(request.category());
        int progress = validatedProgress(request.progress() == null ? 0 : request.progress());
        String status = validatedStatus(request.status() == null ? "PLANNED" : request.status());
        course.setStatus(status);
        course.setProgress(status.equals("COMPLETED") ? 100 : progress);
        course.setGoal(request.goal());
        course.setNotes(request.notes());

        LearningCourse saved = repository.save(course);
        auditLogService.record(currentUser(), currentUser(), "LEARNING_COURSE_CREATED",
                "Created learning course: " + saved.getTitle(), null, null);
        notificationService.createNotification("Course added", saved.getTitle() + " was added to your learning plan.",
            "INFO", "/learning");
        return toResponse(saved);
    }

    @Override
    @Transactional
    public LearningCourseResponse update(Long id, LearningCourseRequest request) {
        LearningCourse course = getByIdAndUser(id);
        course.setTitle(request.title());
        course.setProvider(request.provider());
        course.setCategory(request.category());
        int progress = validatedProgress(request.progress() == null ? course.getProgress() : request.progress());
        String status = validatedStatus(request.status() == null ? course.getStatus() : request.status());
        course.setStatus(status);
        course.setProgress(status.equals("COMPLETED") ? 100 : progress);
        course.setGoal(request.goal());
        course.setNotes(request.notes());

        LearningCourse saved = repository.save(course);
        auditLogService.record(currentUser(), currentUser(), "LEARNING_COURSE_UPDATED",
                "Updated learning course: " + saved.getTitle(), null, null);
        return toResponse(saved);
    }

    @Override
    public LearningCourseResponse getById(Long id) {
        return toResponse(getByIdAndUser(id));
    }

    @Override
    public Page<LearningCourseResponse> getAll(Pageable pageable) {
        return repository.findByUserIdOrderByCreatedAtDesc(currentUser().getId(), pageable).map(this::toResponse);
    }

    @Override
    public Page<LearningCourseResponse> search(String query, Pageable pageable) {
        return repository.searchCourses(currentUser().getId(), query, pageable).map(this::toResponse);
    }

    @Override
    public Page<LearningCourseResponse> getByStatus(String status, Pageable pageable) {
        return repository.findByUserIdAndStatus(currentUser().getId(), status, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LearningCourse course = getByIdAndUser(id);
        repository.delete(course);
        auditLogService.record(currentUser(), currentUser(), "LEARNING_COURSE_DELETED",
                "Deleted learning course: " + course.getTitle(), null, null);
    }

    @Override
    public long count() {
        return repository.countByUserId(currentUser().getId());
    }

    private LearningCourse getByIdAndUser(Long id) {
        LearningCourse course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning course not found"));
        if (!course.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        return course;
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }

    private LearningCourseResponse toResponse(LearningCourse course) {
        return new LearningCourseResponse(
                course.getId(),
                course.getTitle(),
                course.getProvider(),
                course.getCategory(),
                course.getStatus(),
                course.getProgress(),
                course.getGoal(),
                course.getNotes(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }

    private int validatedProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new BadRequestException("Course progress must be between 0 and 100");
        }
        return progress;
    }

    private String validatedStatus(String status) {
        String normalized = status.trim().toUpperCase(java.util.Locale.ROOT);
        if (!STATUSES.contains(normalized)) {
            throw new BadRequestException("Course status must be PLANNED, IN_PROGRESS, or COMPLETED");
        }
        return normalized;
    }
}
