package com.careernest.modules.coding.repository;

import com.careernest.modules.coding.entity.CodingProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodingProfileRepository extends JpaRepository<CodingProfile, Long> {
    long countByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(c.problemsSolved), 0) FROM CodingProfile c WHERE c.user.id = :userId")
    long sumProblemsSolvedByUserId(Long userId);

    Page<CodingProfile> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT c FROM CodingProfile c WHERE c.user.id = :userId AND " +
            "(LOWER(c.platform) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.username) LIKE LOWER(CONCAT('%', :search, '%')))" )
    Page<CodingProfile> searchProfiles(Long userId, String search, Pageable pageable);

    Page<CodingProfile> findByUserIdAndPlatform(Long userId, String platform, Pageable pageable);
}
