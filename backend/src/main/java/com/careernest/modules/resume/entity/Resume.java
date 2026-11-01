package com.careernest.modules.resume.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "resumes", indexes = {
        @Index(name = "idx_resumes_user_id", columnList = "user_id"),
        @Index(name = "idx_resumes_template", columnList = "template"),
        @Index(name = "idx_resumes_is_primary", columnList = "is_primary")
})
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ResumeVersion> versions = new java.util.ArrayList<>();

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "template", nullable = false, length = 50)
    private String template = "MODERN";

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "ats_score")
    private Integer atsScore;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public java.util.List<ResumeVersion> getVersions() { return versions; }
    public void setVersions(java.util.List<ResumeVersion> versions) { this.versions = versions; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }
    public Integer getAtsScore() { return atsScore; }
    public void setAtsScore(Integer atsScore) { this.atsScore = atsScore; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
