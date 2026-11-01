package com.careernest.modules.auth;

import com.careernest.modules.auth.dto.LoginRequest;
import com.careernest.modules.auth.dto.ResetPasswordRequest;
import com.careernest.modules.auth.dto.SignupRequest;
import com.careernest.modules.auth.dto.VerifyEmailRequest;
import com.careernest.modules.auth.entity.EmailVerificationToken;
import com.careernest.modules.auth.entity.PasswordResetToken;
import com.careernest.modules.auth.repository.EmailVerificationTokenRepository;
import com.careernest.modules.auth.repository.PasswordResetTokenRepository;
import com.careernest.modules.auth.service.AuthService;
import com.careernest.common.exception.BadRequestException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.util.HashingUtil;
import com.careernest.modules.user.entity.AccountStatus;
import com.careernest.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceImplTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationTokenRepository emailTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Test
    void signupCreatesPendingVerificationStudentAccount() {
        authService.signup(new SignupRequest(
                "Aarav Sharma",
                "aarav@example.com",
                "StrongPass@123",
                "StrongPass@123"
        ), "127.0.0.1", "JUnit");

        var user = userRepository.findByEmailIgnoreCase("aarav@example.com").orElseThrow();
        assertThat(user.getRole().getName().name()).isEqualTo("STUDENT");
        assertThat(user.isEmailVerified()).isFalse();
        assertThat(user.getAccountStatus()).isEqualTo(AccountStatus.PENDING_VERIFICATION);
        assertThat(user.getPasswordHash()).doesNotContain("StrongPass@123");
        assertThat(user.getPasswordHash()).startsWith("$2a$12$");
    }

    @Test
    void signupRejectsPasswordMismatch() {
        assertThatThrownBy(() -> authService.signup(new SignupRequest(
                "Aarav Sharma",
                "mismatch@example.com",
                "StrongPass@123",
                "WrongPass@123"
        ), "127.0.0.1", "JUnit")).hasMessageContaining("Passwords do not match");
    }

    @Test
    void unverifiedUserCannotLogin() {
        authService.signup(new SignupRequest(
                "Aarav Sharma",
                "login@example.com",
                "StrongPass@123",
                "StrongPass@123"
        ), "127.0.0.1", "JUnit");

        assertThatThrownBy(() -> authService.login(
            new LoginRequest("login@example.com", "StrongPass@123"), "127.0.0.1", "JUnit"))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("not active");
        }

        @Test
        void validVerificationTokenActivatesAccount() {
        authService.signup(new SignupRequest(
            "Aarav Sharma",
            "verify@example.com",
            "StrongPass@123",
            "StrongPass@123"
        ), "127.0.0.1", "JUnit");
        var user = userRepository.findByEmailIgnoreCase("verify@example.com").orElseThrow();
        String rawToken = "known-verification-token";
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(HashingUtil.sha256(rawToken));
        token.setExpiresAt(java.time.Instant.now().plusSeconds(600));
        emailTokenRepository.save(token);

        authService.verifyEmail(new VerifyEmailRequest(rawToken));

        var verifiedUser = userRepository.findByEmailIgnoreCase("verify@example.com").orElseThrow();
        assertThat(verifiedUser.isEmailVerified()).isTrue();
        assertThat(verifiedUser.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

        @Test
        void passwordResetHashesNewPasswordAndTokenCannotBeReused() {
        authService.signup(new SignupRequest(
            "Reset Test User",
            "reset-flow@example.com",
            "StrongPass@123",
            "StrongPass@123"
        ), "127.0.0.1", "JUnit");
        var user = userRepository.findByEmailIgnoreCase("reset-flow@example.com").orElseThrow();
        String rawToken = "known-password-reset-token";
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(HashingUtil.sha256(rawToken));
        resetToken.setExpiresAt(java.time.Instant.now().plusSeconds(600));
        passwordResetTokenRepository.save(resetToken);

        ResetPasswordRequest request = new ResetPasswordRequest(
            rawToken, "UpdatedPass@456", "UpdatedPass@456");
        authService.resetPassword(request);

        var updatedUser = userRepository.findByEmailIgnoreCase("reset-flow@example.com").orElseThrow();
        assertThat(HashingUtil.bcryptMatches("UpdatedPass@456", updatedUser.getPasswordHash())).isTrue();
        assertThat(passwordResetTokenRepository.findTopByTokenHashOrderByCreatedAtDesc(HashingUtil.sha256(rawToken))
            .orElseThrow().getUsedAt()).isNotNull();
        assertThatThrownBy(() -> authService.resetPassword(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Invalid or expired reset token");
        }

        @Test
        void expiredPasswordResetTokenIsRejected() {
        authService.signup(new SignupRequest(
            "Expired Reset User",
            "expired-reset@example.com",
            "StrongPass@123",
            "StrongPass@123"
        ), "127.0.0.1", "JUnit");
        var user = userRepository.findByEmailIgnoreCase("expired-reset@example.com").orElseThrow();
        String rawToken = "expired-password-reset-token";
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(HashingUtil.sha256(rawToken));
        resetToken.setExpiresAt(java.time.Instant.now().minusSeconds(1));
        passwordResetTokenRepository.save(resetToken);

        assertThatThrownBy(() -> authService.resetPassword(new ResetPasswordRequest(
            rawToken, "UpdatedPass@456", "UpdatedPass@456")))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Invalid or expired reset token");
        }
}
