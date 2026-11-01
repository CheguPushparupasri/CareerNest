package com.careernest.modules.auth.repository;

import com.careernest.modules.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findTopByTokenHashOrderByCreatedAtDesc(String tokenHash);
}
