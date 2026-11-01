package com.careernest.modules.learning.repository;

import com.careernest.modules.learning.entity.LearningCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LearningCourseRepository extends JpaRepository<LearningCourse, Long> {
    Page<LearningCourse> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT l FROM LearningCourse l WHERE l.user.id = :userId AND " +
            "(LOWER(l.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(l.category) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(l.status) LIKE LOWER(CONCAT('%', :search, '%')))" )
    Page<LearningCourse> searchCourses(Long userId, String search, Pageable pageable);

    Page<LearningCourse> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    long countByUserId(Long userId);

    @Query("SELECT COALESCE(l.category, 'Uncategorized'), AVG(l.progress), COUNT(l.id) " +
            "FROM LearningCourse l WHERE l.user.id = :userId GROUP BY l.category ORDER BY l.category")
    List<Object[]> findProgressSummaryByUserId(Long userId);

    @Query("SELECT COALESCE(AVG(l.progress), 0) FROM LearningCourse l WHERE l.user.id = :userId")
    double averageProgressByUserId(Long userId);
}
