package com.careernest.modules.internship.service;

import com.careernest.modules.internship.dto.InternshipApplicationRequest;
import com.careernest.modules.internship.dto.InternshipApplicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InternshipApplicationService {
    InternshipApplicationResponse create(InternshipApplicationRequest request);
    InternshipApplicationResponse update(Long id, InternshipApplicationRequest request);
    InternshipApplicationResponse getById(Long id);
    Page<InternshipApplicationResponse> getAll(Pageable pageable);
    Page<InternshipApplicationResponse> search(String query, Pageable pageable);
    Page<InternshipApplicationResponse> getByStatus(String status, Pageable pageable);
    void delete(Long id);
    long count();
}
