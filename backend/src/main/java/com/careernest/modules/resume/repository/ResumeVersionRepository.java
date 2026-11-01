package com.careernest.modules.resume.repository;

import com.careernest.modules.resume.entity.ResumeVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, Long> {
    List<ResumeVersion> findByResumeIdOrderByCreatedAtDesc(Long resumeId);
    void deleteByResumeId(Long resumeId);
}
