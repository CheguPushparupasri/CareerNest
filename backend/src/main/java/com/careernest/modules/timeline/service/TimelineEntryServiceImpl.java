package com.careernest.modules.timeline.service;

import com.careernest.common.exception.BadRequestException;
import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.timeline.dto.TimelineEntryRequest;
import com.careernest.modules.timeline.dto.TimelineEntryResponse;
import com.careernest.modules.timeline.entity.TimelineEntry;
import com.careernest.modules.timeline.repository.TimelineEntryRepository;
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
public class TimelineEntryServiceImpl implements TimelineEntryService {
    private final TimelineEntryRepository repository;
    private final AuditLogService auditLogService;

    public TimelineEntryServiceImpl(TimelineEntryRepository repository, AuditLogService auditLogService) {
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public TimelineEntryResponse create(TimelineEntryRequest request) {
        TimelineEntry entry = new TimelineEntry();
        entry.setUser(currentUser());
        entry.setEntryType(request.entryType());
        entry.setTitle(request.title());
        entry.setDescription(request.description());
        entry.setOrganization(request.organization());
        entry.setEntryDate(parseInstant(request.entryDate()));

        TimelineEntry saved = repository.save(entry);
        auditLogService.record(currentUser(), currentUser(), "TIMELINE_ENTRY_CREATED",
                "Created timeline entry: " + saved.getTitle(), null, null);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TimelineEntryResponse update(Long id, TimelineEntryRequest request) {
        TimelineEntry entry = getByIdAndUser(id);
        entry.setEntryType(request.entryType());
        entry.setTitle(request.title());
        entry.setDescription(request.description());
        entry.setOrganization(request.organization());
        entry.setEntryDate(parseInstant(request.entryDate()));

        TimelineEntry saved = repository.save(entry);
        auditLogService.record(currentUser(), currentUser(), "TIMELINE_ENTRY_UPDATED",
                "Updated timeline entry: " + saved.getTitle(), null, null);
        return toResponse(saved);
    }

    @Override
    public TimelineEntryResponse getById(Long id) {
        return toResponse(getByIdAndUser(id));
    }

    @Override
    public Page<TimelineEntryResponse> getAll(Pageable pageable) {
        return repository.findByUserIdOrderByEntryDateDesc(currentUser().getId(), pageable).map(this::toResponse);
    }

    @Override
    public Page<TimelineEntryResponse> search(String query, Pageable pageable) {
        return repository.searchEntries(currentUser().getId(), query, pageable).map(this::toResponse);
    }

    @Override
    public Page<TimelineEntryResponse> getByType(String type, Pageable pageable) {
        return repository.findByUserIdAndEntryType(currentUser().getId(), type, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TimelineEntry entry = getByIdAndUser(id);
        repository.delete(entry);
        auditLogService.record(currentUser(), currentUser(), "TIMELINE_ENTRY_DELETED",
                "Deleted timeline entry: " + entry.getTitle(), null, null);
    }

    private TimelineEntry getByIdAndUser(Long id) {
        TimelineEntry entry = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timeline entry not found"));
        if (!entry.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        return entry;
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (Exception ignored) {
            try {
                return LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC).toInstant();
            } catch (Exception ex) {
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

    private TimelineEntryResponse toResponse(TimelineEntry entry) {
        return new TimelineEntryResponse(entry.getId(), entry.getEntryType(), entry.getTitle(),
                entry.getDescription(), entry.getOrganization(), entry.getEntryDate(),
                entry.getCreatedAt(), entry.getUpdatedAt());
    }
}
