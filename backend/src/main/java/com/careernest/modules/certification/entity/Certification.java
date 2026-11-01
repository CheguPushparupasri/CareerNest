package com.careernest.modules.certification.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "certifications", indexes = {
        @Index(name = "idx_certifications_user_id", columnList = "user_id"),
        @Index(name = "idx_certifications_category", columnList = "category"),
        @Index(name = "idx_certifications_created_at", columnList = "created_at")
})
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "issuer", nullable = false, length = 200)
    private String issuer;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "credential_url", length = 500)
    private String credentialUrl;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "issue_date")
    private Instant issueDate;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCredentialUrl() { return credentialUrl; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Instant getIssueDate() { return issueDate; }
    public void setIssueDate(Instant issueDate) { this.issueDate = issueDate; }
    public Instant getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
