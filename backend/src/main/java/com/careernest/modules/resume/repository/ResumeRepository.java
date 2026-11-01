package com.careernest.modules.resume.repository;

import com.careernest.modules.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByUserIdAndIsPrimaryTrue(Long userId);
    void deleteByUserId(Long userId);
    long countByUserId(Long userId);
}
