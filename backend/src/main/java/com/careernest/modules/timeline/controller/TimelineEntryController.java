package com.careernest.modules.timeline.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.timeline.dto.TimelineEntryRequest;
import com.careernest.modules.timeline.dto.TimelineEntryResponse;
import com.careernest.modules.timeline.service.TimelineEntryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/timeline")
@Tag(name = "Career Timeline")
public class TimelineEntryController {
    private final TimelineEntryService service;

    public TimelineEntryController(TimelineEntryService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<TimelineEntryResponse> create(@Valid @RequestBody TimelineEntryRequest request) {
        return ApiResponse.success("Timeline entry created successfully", service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TimelineEntryResponse> update(@PathVariable Long id, @Valid @RequestBody TimelineEntryRequest request) {
        return ApiResponse.success("Timeline entry updated successfully", service.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<TimelineEntryResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Timeline entry fetched successfully", service.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<TimelineEntryResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "entryDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success("Timeline entries fetched successfully", service.getAll(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<TimelineEntryResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("entryDate").descending());
        return ApiResponse.success("Search results fetched", service.search(query, pageable));
    }

    @GetMapping("/type/{type}")
    public ApiResponse<Page<TimelineEntryResponse>> getByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("entryDate").descending());
        return ApiResponse.success("Timeline entries by type fetched", service.getByType(type, pageable));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("Timeline entry deleted successfully");
    }
}
