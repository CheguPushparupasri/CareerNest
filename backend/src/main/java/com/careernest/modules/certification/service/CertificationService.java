package com.careernest.modules.certification.service;

import com.careernest.modules.certification.dto.CertificationRequest;
import com.careernest.modules.certification.dto.CertificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CertificationService {
    CertificationResponse create(CertificationRequest request);
    CertificationResponse update(Long id, CertificationRequest request);
    CertificationResponse getById(Long id);
    Page<CertificationResponse> getAll(Pageable pageable);
    Page<CertificationResponse> search(String query, Pageable pageable);
    Page<CertificationResponse> getByCategory(String category, Pageable pageable);
    void delete(Long id);
    long count();
}
