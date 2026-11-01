package com.careernest.modules.project.service;

import com.careernest.modules.project.dto.ScreenshotResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectScreenshotService {
    ScreenshotResponse upload(Long projectId, MultipartFile file);
    void delete(Long screenshotId);
    void reorder(Long projectId, List<Long> screenshotIds);
}
