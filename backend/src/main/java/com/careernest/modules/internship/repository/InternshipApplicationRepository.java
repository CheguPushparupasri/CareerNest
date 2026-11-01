package com.careernest.modules.internship.repository;

import com.careernest.modules.internship.entity.InternshipApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface InternshipApplicationRepository extends JpaRepository<InternshipApplication, Long> {
    Page<InternshipApplication> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT i FROM InternshipApplication i WHERE i.user.id = :userId AND " +
            "(LOWER(i.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.position) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.status) LIKE LOWER(CONCAT('%', :search, '%')))" )
    Page<InternshipApplication> searchApplications(Long userId, String search, Pageable pageable);

    Page<InternshipApplication> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    long countByUserId(Long userId);

    @Query("SELECT i.status, COUNT(i.id) FROM InternshipApplication i WHERE i.user.id = :userId GROUP BY i.status ORDER BY i.status")
    List<Object[]> countStatusesByUserId(Long userId);

    List<InternshipApplication> findTop5ByUserIdAndDeadlineAfterOrderByDeadlineAsc(Long userId, Instant deadline);
}
