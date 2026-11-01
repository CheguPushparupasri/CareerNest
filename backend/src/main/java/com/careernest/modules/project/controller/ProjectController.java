package com.careernest.modules.project.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.project.dto.ProjectListResponse;
import com.careernest.modules.project.dto.ProjectRequest;
import com.careernest.modules.project.dto.ProjectResponse;
import com.careernest.modules.project.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success("Project created successfully", projectService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProjectResponse> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success("Project updated successfully", projectService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Project fetched successfully", projectService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<ProjectListResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success("Projects fetched successfully", projectService.getAll(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<ProjectListResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Search results fetched", projectService.search(query, pageable));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<Page<ProjectListResponse>> getByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Projects by status fetched", projectService.getByStatus(status, pageable));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ApiResponse.success("Project deleted successfully");
    }

    @PatchMapping("/{id}/featured")
    public ApiResponse<Void> toggleFeatured(@PathVariable Long id) {
        projectService.toggleFeatured(id);
        return ApiResponse.success("Project featured status updated");
    }
}
