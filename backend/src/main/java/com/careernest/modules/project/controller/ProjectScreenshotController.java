package com.careernest.modules.project.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.project.dto.ScreenshotResponse;
import com.careernest.modules.project.service.ProjectScreenshotService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/screenshots")
@Tag(name = "Project Screenshots")
public class ProjectScreenshotController {
    private final ProjectScreenshotService screenshotService;

    public ProjectScreenshotController(ProjectScreenshotService screenshotService) {
        this.screenshotService = screenshotService;
    }

    @PostMapping
    public ApiResponse<ScreenshotResponse> upload(@PathVariable Long projectId, @RequestPart("file") MultipartFile file) {
        return ApiResponse.success("Screenshot uploaded successfully", screenshotService.upload(projectId, file));
    }

    @DeleteMapping("/{screenshotId}")
    public ApiResponse<Void> delete(@PathVariable Long projectId, @PathVariable Long screenshotId) {
        screenshotService.delete(screenshotId);
        return ApiResponse.success("Screenshot deleted successfully");
    }

    @PutMapping("/reorder")
    public ApiResponse<Void> reorder(@PathVariable Long projectId, @RequestBody List<Long> screenshotIds) {
        screenshotService.reorder(projectId, screenshotIds);
        return ApiResponse.success("Screenshots reordered successfully");
    }
}
