package com.careernest.modules.timeline.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "timeline_entries", indexes = {
        @Index(name = "idx_timeline_entries_user_id", columnList = "user_id"),
        @Index(name = "idx_timeline_entries_type", columnList = "entry_type"),
        @Index(name = "idx_timeline_entries_date", columnList = "entry_date")
})
public class TimelineEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "entry_type", nullable = false, length = 50)
    private String entryType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "organization", length = 200)
    private String organization;

    @Column(name = "entry_date")
    private Instant entryDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public Instant getEntryDate() { return entryDate; }
    public void setEntryDate(Instant entryDate) { this.entryDate = entryDate; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
