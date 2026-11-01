package com.careernest.modules.project.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_projects_user_id", columnList = "user_id"),
        @Index(name = "idx_projects_status", columnList = "status"),
        @Index(name = "idx_projects_created_at", columnList = "created_at")
})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ProjectScreenshot> screenshots = new java.util.ArrayList<>();

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "live_demo_url", length = 500)
    private String liveDemoUrl;

    @Column(name = "tech_stack", length = 500)
    private String techStack;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "IN_PROGRESS";

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "featured", nullable = false)
    private boolean featured = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public java.util.List<ProjectScreenshot> getScreenshots() { return screenshots; }
    public void setScreenshots(java.util.List<ProjectScreenshot> screenshots) { this.screenshots = screenshots; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getLiveDemoUrl() { return liveDemoUrl; }
    public void setLiveDemoUrl(String liveDemoUrl) { this.liveDemoUrl = liveDemoUrl; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
