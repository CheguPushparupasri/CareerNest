package com.careernest.modules.project.repository;

import com.careernest.modules.project.entity.ProjectScreenshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectScreenshotRepository extends JpaRepository<ProjectScreenshot, Long> {
    List<ProjectScreenshot> findByProjectIdOrderByDisplayOrderAsc(Long projectId);
    void deleteByProjectId(Long projectId);
}
