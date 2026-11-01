package com.careernest.modules.auth.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_sessions", indexes = @Index(name = "idx_user_sessions_user_id", columnList = "user_id"))
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "device_name", length = 120)
    private String deviceName;
    @Column(name = "ip_address", length = 80)
    private String ipAddress;
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    @Column(name = "login_at", nullable = false)
    private Instant loginAt = Instant.now();
    @Column(name = "last_used_at", nullable = false)
    private Instant lastUsedAt = Instant.now();
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;
    @Column(name = "revoked_reason")
    private String revokedReason;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
    @PreUpdate void preUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public Instant getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public void revoke(String reason) { this.revokedAt = Instant.now(); this.revokedReason = reason; }
}
