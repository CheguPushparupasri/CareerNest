package com.careernest.modules.coding.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.coding.dto.CodingProfileRequest;
import com.careernest.modules.coding.dto.CodingProfileResponse;
import com.careernest.modules.coding.service.CodingProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coding-profiles")
@Tag(name = "Coding Profiles")
public class CodingProfileController {
    private final CodingProfileService service;

    public CodingProfileController(CodingProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<CodingProfileResponse> create(@Valid @RequestBody CodingProfileRequest request) {
        return ApiResponse.success("Coding profile created successfully", service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CodingProfileResponse> update(@PathVariable Long id, @Valid @RequestBody CodingProfileRequest request) {
        return ApiResponse.success("Coding profile updated successfully", service.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<CodingProfileResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Coding profile fetched successfully", service.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<CodingProfileResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success("Coding profiles fetched successfully", service.getAll(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<CodingProfileResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Search results fetched", service.search(query, pageable));
    }

    @GetMapping("/platform/{platform}")
    public ApiResponse<Page<CodingProfileResponse>> getByPlatform(
            @PathVariable String platform,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success("Coding profiles by platform fetched", service.getByPlatform(platform, pageable));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("Coding profile deleted successfully");
    }
}
