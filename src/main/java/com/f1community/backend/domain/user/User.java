package com.f1community.backend.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String username;

    @Setter
    private String email;

    @Setter
    private String password;

    @Setter
    @Column(name = "profile_image")
    private String profileImage;

    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Setter
    private String country;
    private String timezone;

    @Setter
    private String firstName;

    @Setter
    private String lastName;

    @Setter
    private LocalDate birthDate;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @Setter
    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @Setter
    @Column(name = "favorite_team")
    private String favoriteTeam;

    @Setter
    @Column(name = "favorite_driver")
    private String favoriteDriver;

    @Builder
    public User(Long id, String username, String email, String password, String profileImage,
                Role role, String country, String timezone, String firstName, String lastName,
                LocalDate birthDate, UserStatus status, LocalDateTime createdAt,
                LocalDateTime updatedAt, LocalDateTime lastLoginAt,
                String favoriteTeam, String favoriteDriver) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.role = role;
        this.country = country;
        this.timezone = timezone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
        this.favoriteTeam = favoriteTeam;
        this.favoriteDriver = favoriteDriver;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now().withNano(0);
        this.updatedAt = LocalDateTime.now().withNano(0);
        this.lastLoginAt = LocalDateTime.now().withNano(0);
        if (this.role == null) this.role = Role.USER;
        if (this.status == null) this.status = UserStatus.ACTIVE;
        if (this.timezone == null) this.timezone = "UTC";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}