package com.careernest.modules.profile.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 160)
    private String headline;

    @Column(length = 30)
    private String phone;

    @Column(length = 120)
    private String location;

    @Column(name = "education_summary", length = 255)
    private String educationSummary;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Column(name = "profile_photo_public_id")
    private String profilePhotoPublicId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getEducationSummary() { return educationSummary; }
    public void setEducationSummary(String educationSummary) { this.educationSummary = educationSummary; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }
    public String getProfilePhotoPublicId() { return profilePhotoPublicId; }
    public void setProfilePhotoPublicId(String profilePhotoPublicId) { this.profilePhotoPublicId = profilePhotoPublicId; }
}
