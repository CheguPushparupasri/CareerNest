package com.careernest.modules.audit.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "audit_logs", indexes = @Index(name = "idx_audit_logs_user_id", columnList = "user_id"))
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id")
    private User actor;
    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;
    @Column(name = "event_description", length = 500)
    private String eventDescription;
    @Column(name = "ip_address", length = 80)
    private String ipAddress;
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    public Long getId() { return id; }
    public User getUser() { return user; }
    public User getActor() { return actor; }
    public String getEventType() { return eventType; }
    public String getEventDescription() { return eventDescription; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Instant getCreatedAt() { return createdAt; }
    public void setUser(User user) { this.user = user; }
    public void setActor(User actor) { this.actor = actor; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
