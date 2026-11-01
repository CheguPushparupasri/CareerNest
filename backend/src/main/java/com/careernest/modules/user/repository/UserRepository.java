package com.careernest.modules.user.repository;

import com.careernest.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPublicId(String publicId);
    boolean existsByEmailIgnoreCase(String email);
    long countByDeletedAtIsNull();
    Page<User> findByDeletedAtIsNull(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> searchActiveUsers(@Param("query") String query, Pageable pageable);
}
