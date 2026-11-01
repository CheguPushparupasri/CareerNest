package com.careernest.modules.certification.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.certification.dto.CertificationRequest;
import com.careernest.modules.certification.dto.CertificationResponse;
import com.careernest.modules.certification.service.CertificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/certifications")
@Tag(name = "Certifications")
public class CertificationController {
    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @PostMapping
    public ApiResponse<CertificationResponse> create(@Valid @RequestBody CertificationRequest request) {
        return ApiResponse.success("Certification created successfully", certificationService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CertificationResponse> update(@PathVariable Long id, @Valid @RequestBody CertificationRequest request) {
        return ApiResponse.success("Certification updated successfully", certificationService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<CertificationResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Certification fetched successfully", certificationService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<CertificationResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success("Certifications fetched successfully", certificationService.getAll(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<CertificationResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Search results fetched", certificationService.search(query, pageable));
    }

    @GetMapping("/category/{category}")
    public ApiResponse<Page<CertificationResponse>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Certifications by category fetched", certificationService.getByCategory(category, pageable));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        certificationService.delete(id);
        return ApiResponse.success("Certification deleted successfully");
    }

    @GetMapping("/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success("Certification count fetched", certificationService.count());
    }
}
