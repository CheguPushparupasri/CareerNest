package com.careernest.modules.user.entity;

import com.careernest.modules.certification.entity.Certification;
import com.careernest.modules.coding.entity.CodingProfile;
import com.careernest.modules.internship.entity.InternshipApplication;
import com.careernest.modules.learning.entity.LearningCourse;
import com.careernest.modules.notification.entity.Notification;
import com.careernest.modules.profile.entity.UserProfile;
import com.careernest.modules.project.entity.Project;
import com.careernest.modules.resume.entity.Resume;
import com.careernest.modules.timeline.entity.TimelineEntry;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_role_id", columnList = "role_id"),
        @Index(name = "idx_users_account_status", columnList = "account_status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_users_public_id", columnNames = "public_id")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, length = 64)
    private String publicId = "usr_" + UUID.randomUUID().toString().replace("-", "");

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodingProfile> codingProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InternshipApplication> internshipApplications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningCourse> learningCourses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resumes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimelineEntry> timelineEntries = new ArrayList<>();

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 30)
    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "profile_setup_completed", nullable = false)
    private boolean profileSetupCompleted;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getId() { return id; }
    public String getPublicId() { return publicId; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }
    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }
    public List<CodingProfile> getCodingProfiles() { return codingProfiles; }
    public void setCodingProfiles(List<CodingProfile> codingProfiles) { this.codingProfiles = codingProfiles; }
    public List<InternshipApplication> getInternshipApplications() { return internshipApplications; }
    public void setInternshipApplications(List<InternshipApplication> internshipApplications) { this.internshipApplications = internshipApplications; }
    public List<LearningCourse> getLearningCourses() { return learningCourses; }
    public void setLearningCourses(List<LearningCourse> learningCourses) { this.learningCourses = learningCourses; }
    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    public List<Resume> getResumes() { return resumes; }
    public void setResumes(List<Resume> resumes) { this.resumes = resumes; }
    public List<TimelineEntry> getTimelineEntries() { return timelineEntries; }
    public void setTimelineEntries(List<TimelineEntry> timelineEntries) { this.timelineEntries = timelineEntries; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email.toLowerCase(); }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public boolean isProfileSetupCompleted() { return profileSetupCompleted; }
    public void setProfileSetupCompleted(boolean profileSetupCompleted) { this.profileSetupCompleted = profileSetupCompleted; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public Instant getCreatedAt() { return createdAt; }
}
