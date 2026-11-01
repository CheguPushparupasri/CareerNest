package com.careernest.modules.coding.service;

import com.careernest.modules.coding.dto.CodingProfileRequest;
import com.careernest.modules.coding.dto.CodingProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CodingProfileService {
    CodingProfileResponse create(CodingProfileRequest request);
    CodingProfileResponse update(Long id, CodingProfileRequest request);
    CodingProfileResponse getById(Long id);
    Page<CodingProfileResponse> getAll(Pageable pageable);
    Page<CodingProfileResponse> search(String query, Pageable pageable);
    Page<CodingProfileResponse> getByPlatform(String platform, Pageable pageable);
    void delete(Long id);
}
