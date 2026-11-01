package com.careernest.modules.resume.service;

import com.careernest.modules.resume.dto.ResumeRequest;
import com.careernest.modules.resume.dto.ResumeResponse;
import com.careernest.modules.resume.dto.ResumeVersionResponse;

import java.util.List;

public interface ResumeService {
    ResumeResponse create(ResumeRequest request);
    ResumeResponse update(Long id, ResumeRequest request);
    ResumeResponse getById(Long id);
    List<ResumeResponse> getAll();
    void delete(Long id);
    void setPrimary(Long id);
    ResumeResponse getPrimary();
    List<ResumeVersionResponse> getVersions(Long resumeId);
    ResumeResponse exportToPdf(Long id);
}
