package com.careernest.modules.internship.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.internship.dto.InternshipApplicationRequest;
import com.careernest.modules.internship.dto.InternshipApplicationResponse;
import com.careernest.modules.internship.service.InternshipApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internships")
@Tag(name = "Internships")
public class InternshipApplicationController {
    private final InternshipApplicationService service;

    public InternshipApplicationController(InternshipApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<InternshipApplicationResponse> create(@Valid @RequestBody InternshipApplicationRequest request) {
        return ApiResponse.success("Application created successfully", service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<InternshipApplicationResponse> update(@PathVariable Long id, @Valid @RequestBody InternshipApplicationRequest request) {
        return ApiResponse.success("Application updated successfully", service.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<InternshipApplicationResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Application fetched successfully", service.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<InternshipApplicationResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success("Applications fetched successfully", service.getAll(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<InternshipApplicationResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Search results fetched", service.search(query, pageable));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<Page<InternshipApplicationResponse>> getByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Applications by status fetched", service.getByStatus(status, pageable));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("Application deleted successfully");
    }

    @GetMapping("/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success("Application count fetched", service.count());
    }
}
