package com.careernest.modules.resume.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "resume_versions", indexes = @Index(name = "idx_resume_versions_resume_id", columnList = "resume_id"))
public class ResumeVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "change_description", length = 500)
    private String changeDescription;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }
    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }
    public Instant getCreatedAt() { return createdAt; }
}
