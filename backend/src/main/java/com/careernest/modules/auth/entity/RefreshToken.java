package com.careernest.modules.auth.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
        @Index(name = "idx_refresh_tokens_session_id", columnList = "session_id")
})
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private UserSession session;
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;
    @Column(name = "replaced_by_token_id")
    private Long replacedByTokenId;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public UserSession getSession() { return session; }
    public void setSession(UserSession session) { this.session = session; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public void revoke(Long replacementId) { this.revokedAt = Instant.now(); this.replacedByTokenId = replacementId; }
    public boolean isActive() { return revokedAt == null && expiresAt.isAfter(Instant.now()); }
}
