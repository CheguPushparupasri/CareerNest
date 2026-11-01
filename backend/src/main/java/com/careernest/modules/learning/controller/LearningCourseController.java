package com.careernest.modules.learning.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.learning.dto.LearningCourseRequest;
import com.careernest.modules.learning.dto.LearningCourseResponse;
import com.careernest.modules.learning.service.LearningCourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning")
@Tag(name = "Learning")
public class LearningCourseController {
    private final LearningCourseService service;

    public LearningCourseController(LearningCourseService service) {
        this.service = service;
    }

    @PostMapping("/courses")
    public ApiResponse<LearningCourseResponse> create(@Valid @RequestBody LearningCourseRequest request) {
        return ApiResponse.success("Course created successfully", service.create(request));
    }

    @PutMapping("/courses/{id}")
    public ApiResponse<LearningCourseResponse> update(@PathVariable Long id, @Valid @RequestBody LearningCourseRequest request) {
        return ApiResponse.success("Course updated successfully", service.update(id, request));
    }

    @GetMapping("/courses/{id}")
    public ApiResponse<LearningCourseResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Course fetched successfully", service.getById(id));
    }

    @GetMapping("/courses")
    public ApiResponse<Page<LearningCourseResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success("Courses fetched successfully", service.getAll(pageable));
    }

    @GetMapping("/courses/search")
    public ApiResponse<Page<LearningCourseResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Search results fetched", service.search(query, pageable));
    }

    @GetMapping("/courses/status/{status}")
    public ApiResponse<Page<LearningCourseResponse>> getByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Courses by status fetched", service.getByStatus(status, pageable));
    }

    @DeleteMapping("/courses/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("Course deleted successfully");
    }

    @GetMapping("/courses/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success("Course count fetched", service.count());
    }
}
