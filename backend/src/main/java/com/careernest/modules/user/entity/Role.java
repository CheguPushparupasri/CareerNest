package com.careernest.modules.user.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(name = "uk_roles_name", columnNames = "name"))
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoleName name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Long getId() { return id; }
    public RoleName getName() { return name; }
    public void setName(RoleName name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
