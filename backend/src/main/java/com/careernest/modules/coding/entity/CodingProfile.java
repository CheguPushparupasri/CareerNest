package com.careernest.modules.coding.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "coding_profiles", indexes = {
        @Index(name = "idx_coding_profiles_user_id", columnList = "user_id"),
        @Index(name = "idx_coding_profiles_platform", columnList = "platform")
})
public class CodingProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "platform", nullable = false, length = 100)
    private String platform;

    @Column(name = "username", nullable = false, length = 200)
    private String username;

    @Column(name = "profile_url", length = 500)
    private String profileUrl;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "problems_solved")
    private Integer problemsSolved;

    @Column(name = "streak")
    private Integer streak;

    @Column(name = "badge_count")
    private Integer badgeCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getProblemsSolved() { return problemsSolved; }
    public void setProblemsSolved(Integer problemsSolved) { this.problemsSolved = problemsSolved; }
    public Integer getStreak() { return streak; }
    public void setStreak(Integer streak) { this.streak = streak; }
    public Integer getBadgeCount() { return badgeCount; }
    public void setBadgeCount(Integer badgeCount) { this.badgeCount = badgeCount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
