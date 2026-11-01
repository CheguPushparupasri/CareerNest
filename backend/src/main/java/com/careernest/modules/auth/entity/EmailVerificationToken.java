package com.careernest.modules.auth.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "email_verification_tokens", indexes = @Index(name = "idx_email_tokens_user_id", columnList = "user_id"))
public class EmailVerificationToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "verified_at")
    private Instant verifiedAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Instant getVerifiedAt() { return verifiedAt; }
    public void markVerified() { this.verifiedAt = Instant.now(); }
}
