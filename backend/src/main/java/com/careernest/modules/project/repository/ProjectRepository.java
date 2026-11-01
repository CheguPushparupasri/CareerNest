package com.careernest.modules.project.repository;

import com.careernest.modules.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Project> findByUserIdAndFeaturedTrueOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
    
    @Query("SELECT p FROM Project p WHERE p.user.id = :userId AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.techStack) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> searchProjects(Long userId, String search, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.user.id = :userId AND p.status = :status")
    Page<Project> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
}
