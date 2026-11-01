package com.careernest.modules.auth.service;

import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.util.HashingUtil;
import com.careernest.common.util.TokenGenerator;
import com.careernest.modules.auth.entity.RefreshToken;
import com.careernest.modules.auth.entity.UserSession;
import com.careernest.modules.auth.repository.RefreshTokenRepository;
import com.careernest.modules.auth.repository.UserSessionRepository;
import com.careernest.modules.user.entity.AccountStatus;
import com.careernest.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final long refreshTokenDays;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserSessionRepository userSessionRepository,
                               @Value("${app.jwt.refresh-token-days}") long refreshTokenDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userSessionRepository = userSessionRepository;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public IssuedRefreshToken issue(User user, String ipAddress, String userAgent) {
        Instant expiresAt = Instant.now().plusSeconds(refreshTokenDays * 24 * 60 * 60);
        UserSession session = new UserSession();
        session.setUser(user);
        session.setDeviceName(resolveDevice(userAgent));
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(expiresAt);
        userSessionRepository.save(session);
        return createToken(user, session, expiresAt);
    }

    @Transactional
    public IssuedRefreshToken rotate(String rawToken) {
        RefreshToken existing = refreshTokenRepository.findByTokenHash(HashingUtil.sha256(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (!existing.isActive() || existing.getSession().getRevokedAt() != null) {
            throw new UnauthorizedException("Refresh token is expired or revoked");
        }
        if (!existing.getUser().isEmailVerified() || existing.getUser().getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedException("Account is not active");
        }
        existing.getSession().setLastUsedAt(Instant.now());
        IssuedRefreshToken replacement = createToken(existing.getUser(), existing.getSession(), existing.getExpiresAt());
        existing.revoke(replacement.entity().getId());
        refreshTokenRepository.save(existing);
        return replacement;
    }

    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(HashingUtil.sha256(rawToken)).ifPresent(token -> {
            token.revoke(null);
            token.getSession().revoke("User logout");
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public void revokeAll(User user) {
        refreshTokenRepository.revokeAllActiveByUserId(user.getId(), Instant.now());
        userSessionRepository.findByUserIdAndRevokedAtIsNullOrderByLastUsedAtDesc(user.getId())
                .forEach(session -> session.revoke("All sessions revoked"));
    }

    private IssuedRefreshToken createToken(User user, UserSession session, Instant expiresAt) {
        String rawToken = TokenGenerator.opaqueToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setSession(session);
        refreshToken.setTokenHash(HashingUtil.sha256(rawToken));
        refreshToken.setExpiresAt(expiresAt);
        refreshTokenRepository.save(refreshToken);
        return new IssuedRefreshToken(rawToken, refreshToken);
    }

    private String resolveDevice(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown device";
        }
        return userAgent.length() > 120 ? userAgent.substring(0, 120) : userAgent;
    }

    public record IssuedRefreshToken(String rawToken, RefreshToken entity) {
    }
}
