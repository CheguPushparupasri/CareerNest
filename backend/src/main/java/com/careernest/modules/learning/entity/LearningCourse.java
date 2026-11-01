package com.careernest.modules.learning.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "learning_courses", indexes = {
        @Index(name = "idx_learning_courses_user_id", columnList = "user_id"),
        @Index(name = "idx_learning_courses_status", columnList = "status")
})
public class LearningCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "provider", length = 200)
    private String provider;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "PLANNED";

    @Column(name = "progress", nullable = false)
    private int progress = 0;

    @Column(name = "goal", length = 500)
    private String goal;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
