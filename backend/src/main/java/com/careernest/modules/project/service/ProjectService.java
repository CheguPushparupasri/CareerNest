package com.careernest.modules.project.service;

import com.careernest.modules.project.dto.ProjectListResponse;
import com.careernest.modules.project.dto.ProjectRequest;
import com.careernest.modules.project.dto.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponse create(ProjectRequest request);
    ProjectResponse update(Long id, ProjectRequest request);
    ProjectResponse getById(Long id);
    Page<ProjectListResponse> getAll(Pageable pageable);
    Page<ProjectListResponse> search(String query, Pageable pageable);
    Page<ProjectListResponse> getByStatus(String status, Pageable pageable);
    void delete(Long id);
    void toggleFeatured(Long id);
}
