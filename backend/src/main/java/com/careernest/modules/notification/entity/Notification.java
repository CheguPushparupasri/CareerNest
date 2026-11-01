package com.careernest.modules.notification.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_user_id", columnList = "user_id"),
        @Index(name = "idx_notifications_read", columnList = "is_read")
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public Instant getReadAt() { return readAt; }
    public void markAsRead() { this.isRead = true; this.readAt = Instant.now(); }
    public Instant getCreatedAt() { return createdAt; }
}
