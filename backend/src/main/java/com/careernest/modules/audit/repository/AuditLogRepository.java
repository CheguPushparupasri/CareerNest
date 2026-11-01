package com.careernest.modules.audit.repository;

import com.careernest.modules.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.Instant;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    long countByUserId(Long userId);
    List<AuditLog> findTop8ByUserIdOrderByCreatedAtDesc(Long userId);
    List<AuditLog> findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(Long userId, Instant createdAt);
    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE " +
           "LOWER(a.eventType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.eventDescription) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.ipAddress) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<AuditLog> searchLogs(@Param("query") String query, Pageable pageable);
}
