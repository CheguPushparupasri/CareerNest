package com.careernest.modules.auth.service;

import com.careernest.common.exception.BadRequestException;
import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.common.security.JwtTokenProvider;
import com.careernest.common.util.HashingUtil;
import com.careernest.common.util.TokenGenerator;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.auth.dto.*;
import com.careernest.modules.auth.entity.EmailVerificationToken;
import com.careernest.modules.auth.entity.PasswordResetToken;
import com.careernest.modules.auth.repository.EmailVerificationTokenRepository;
import com.careernest.modules.auth.repository.PasswordResetTokenRepository;
import com.careernest.modules.auth.repository.UserSessionRepository;
import com.careernest.modules.profile.entity.UserProfile;
import com.careernest.modules.profile.repository.UserProfileRepository;
import com.careernest.modules.user.dto.UserResponse;
import com.careernest.modules.user.entity.AccountStatus;
import com.careernest.modules.user.entity.RoleName;
import com.careernest.modules.user.entity.User;
import com.careernest.modules.user.repository.UserRepository;
import com.careernest.modules.user.service.RoleService;
import com.careernest.modules.user.service.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final EmailVerificationTokenRepository emailTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    public AuthServiceImpl(UserRepository userRepository, UserProfileRepository userProfileRepository,
                           EmailVerificationTokenRepository emailTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           UserSessionRepository userSessionRepository, RoleService roleService,
                           PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService,
                           EmailService emailService, UserMapper userMapper, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.emailTokenRepository = emailTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userSessionRepository = userSessionRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public UserResponse signup(SignupRequest request, String ipAddress, String userAgent) {
        ensurePasswordsMatch(request.password(), request.confirmPassword());
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BadRequestException("Email is already registered");
        }
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(roleService.required(RoleName.STUDENT));
        user.setEmailVerified(false);
        user.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
        userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        userProfileRepository.save(profile);

        String token = createEmailVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), token);
        auditLogService.record(user, user, "SIGNUP", "User registered", ipAddress, userAgent);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!user.isEmailVerified() || user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedException("Account is not active");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        user.setLastLoginAt(Instant.now());
        RefreshTokenService.IssuedRefreshToken refreshToken = refreshTokenService.issue(user, ipAddress, userAgent);
        auditLogService.record(user, user, "LOGIN", "User logged in", ipAddress, userAgent);
        return authResponse(user, refreshToken.rawToken());
    }

    @Override
    @Transactional
    public AuthResponse refresh(TokenRefreshRequest request) {
        var issued = refreshTokenService.rotate(request.refreshToken());
        return authResponse(issued.entity().getUser(), issued.rawToken());
    }

    @Override
    @Transactional
    public void logout(TokenRefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }

    @Override
    @Transactional
    public void logoutAll() {
        refreshTokenService.revokeAll(currentUser());
    }

    @Override
    public UserResponse me() {
        return userMapper.toResponse(currentUser());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            String token = TokenGenerator.opaqueToken();
            PasswordResetToken reset = new PasswordResetToken();
            reset.setUser(user);
            reset.setTokenHash(HashingUtil.sha256(token));
            reset.setExpiresAt(Instant.now().plusSeconds(30 * 60));
            passwordResetTokenRepository.save(reset);
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        ensurePasswordsMatch(request.newPassword(), request.confirmPassword());
        PasswordResetToken token = passwordResetTokenRepository.findTopByTokenHashOrderByCreatedAtDesc(HashingUtil.sha256(request.token()))
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));
        if (token.getUsedAt() != null || token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Invalid or expired reset token");
        }
        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        token.markUsed();
        refreshTokenService.revokeAll(user);
        auditLogService.record(user, user, "PASSWORD_RESET", "Password reset completed", null, null);
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        EmailVerificationToken token = emailTokenRepository.findTopByTokenHashOrderByCreatedAtDesc(HashingUtil.sha256(request.token()))
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification token"));
        if (token.getVerifiedAt() != null || token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Invalid or expired verification token");
        }
        User user = token.getUser();
        user.setEmailVerified(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        token.markVerified();
        auditLogService.record(user, user, "EMAIL_VERIFIED", "Email verified", null, null);
    }

    @Override
    @Transactional
    public void resendVerification(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.email())
            .filter(user -> !user.isEmailVerified())
            .ifPresent(user -> emailService.sendVerificationEmail(user.getEmail(), createEmailVerificationToken(user)));
    }

    @Override
    public List<SessionResponse> sessions() {
        return userSessionRepository.findByUserIdAndRevokedAtIsNullOrderByLastUsedAtDesc(currentUser().getId()).stream()
                .map(session -> new SessionResponse(session.getId(), session.getDeviceName(), session.getIpAddress(),
                        session.getUserAgent(), session.getLastUsedAt(), session.getExpiresAt()))
                .toList();
    }

    @Override
    @Transactional
    public void revokeSession(Long sessionId) {
        User user = currentUser();
        var session = userSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        session.revoke("User revoked session");
    }

    private AuthResponse authResponse(User user, String refreshToken) {
        return new AuthResponse(jwtTokenProvider.createAccessToken(user), refreshToken, "Bearer",
                jwtTokenProvider.getAccessTokenSeconds(), userMapper.toResponse(user));
    }

    private String createEmailVerificationToken(User user) {
        String token = TokenGenerator.opaqueToken();
        EmailVerificationToken entity = new EmailVerificationToken();
        entity.setUser(user);
        entity.setTokenHash(HashingUtil.sha256(token));
        entity.setExpiresAt(Instant.now().plusSeconds(24 * 60 * 60));
        emailTokenRepository.save(entity);
        return token;
    }

    private void ensurePasswordsMatch(String first, String second) {
        if (!first.equals(second)) {
            throw new BadRequestException("Passwords do not match");
        }
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }
}
