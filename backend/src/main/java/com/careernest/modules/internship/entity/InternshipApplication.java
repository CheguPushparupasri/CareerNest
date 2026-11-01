package com.careernest.modules.internship.entity;

import com.careernest.modules.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "internship_applications", indexes = {
        @Index(name = "idx_internship_applications_user_id", columnList = "user_id"),
        @Index(name = "idx_internship_applications_status", columnList = "status"),
        @Index(name = "idx_internship_applications_deadline", columnList = "deadline")
})
public class InternshipApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "position", nullable = false, length = 200)
    private String position;

    @Column(name = "role_type", length = 100)
    private String roleType;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "APPLIED";

    @Column(name = "source", length = 200)
    private String source;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "application_date")
    private Instant applicationDate = Instant.now();

    @Column(name = "deadline")
    private Instant deadline;

    @Column(name = "interview_stage", length = 100)
    private String interviewStage = "APPLIED";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getApplicationDate() { return applicationDate; }
    public void setApplicationDate(Instant applicationDate) { this.applicationDate = applicationDate; }
    public Instant getDeadline() { return deadline; }
    public void setDeadline(Instant deadline) { this.deadline = deadline; }
    public String getInterviewStage() { return interviewStage; }
    public void setInterviewStage(String interviewStage) { this.interviewStage = interviewStage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
