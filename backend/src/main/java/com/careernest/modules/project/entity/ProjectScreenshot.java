package com.careernest.modules.project.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "project_screenshots", indexes = @Index(name = "idx_screenshots_project_id", columnList = "project_id"))
public class ProjectScreenshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "public_id", length = 255)
    private String publicId;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public Instant getCreatedAt() { return createdAt; }
}
