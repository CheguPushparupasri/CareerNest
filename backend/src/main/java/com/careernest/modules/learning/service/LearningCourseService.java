package com.careernest.modules.learning.service;

import com.careernest.modules.learning.dto.LearningCourseRequest;
import com.careernest.modules.learning.dto.LearningCourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LearningCourseService {
    LearningCourseResponse create(LearningCourseRequest request);
    LearningCourseResponse update(Long id, LearningCourseRequest request);
    LearningCourseResponse getById(Long id);
    Page<LearningCourseResponse> getAll(Pageable pageable);
    Page<LearningCourseResponse> search(String query, Pageable pageable);
    Page<LearningCourseResponse> getByStatus(String status, Pageable pageable);
    void delete(Long id);
    long count();
}
