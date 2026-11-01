package com.careernest.modules.resume.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.resume.dto.ResumeRequest;
import com.careernest.modules.resume.dto.ResumeResponse;
import com.careernest.modules.resume.dto.ResumeVersionResponse;
import com.careernest.modules.resume.service.ResumeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes")
@Tag(name = "Resumes")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ApiResponse<ResumeResponse> create(@Valid @RequestBody ResumeRequest request) {
        return ApiResponse.success("Resume created successfully", resumeService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResumeResponse> update(@PathVariable Long id, @Valid @RequestBody ResumeRequest request) {
        return ApiResponse.success("Resume updated successfully", resumeService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResumeResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Resume fetched successfully", resumeService.getById(id));
    }

    @GetMapping
    public ApiResponse<List<ResumeResponse>> getAll() {
        return ApiResponse.success("Resumes fetched successfully", resumeService.getAll());
    }

    @GetMapping("/primary")
    public ApiResponse<ResumeResponse> getPrimary() {
        return ApiResponse.success("Primary resume fetched successfully", resumeService.getPrimary());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        resumeService.delete(id);
        return ApiResponse.success("Resume deleted successfully");
    }

    @PatchMapping("/{id}/primary")
    public ApiResponse<Void> setPrimary(@PathVariable Long id) {
        resumeService.setPrimary(id);
        return ApiResponse.success("Primary resume set successfully");
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<List<ResumeVersionResponse>> getVersions(@PathVariable Long id) {
        return ApiResponse.success("Resume versions fetched successfully", resumeService.getVersions(id));
    }

    @GetMapping("/{id}/export")
    public ApiResponse<ResumeResponse> exportToPdf(@PathVariable Long id) {
        return ApiResponse.success("Resume exported successfully", resumeService.exportToPdf(id));
    }
}
