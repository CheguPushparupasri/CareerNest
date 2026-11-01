package com.careernest.modules.certification.repository;

import com.careernest.modules.certification.entity.Certification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Page<Certification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT c FROM Certification c WHERE c.user.id = :userId AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.issuer) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%')))" )
    Page<Certification> searchCertifications(Long userId, String search, Pageable pageable);

    Page<Certification> findByUserIdAndCategory(Long userId, String category, Pageable pageable);
    long countByUserId(Long userId);
}
