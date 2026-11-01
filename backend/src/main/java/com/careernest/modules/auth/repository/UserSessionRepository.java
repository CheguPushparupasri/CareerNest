package com.careernest.modules.auth.repository;

import com.careernest.modules.auth.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserIdAndRevokedAtIsNullOrderByLastUsedAtDesc(Long userId);
    Optional<UserSession> findByIdAndUserId(Long id, Long userId);
}
