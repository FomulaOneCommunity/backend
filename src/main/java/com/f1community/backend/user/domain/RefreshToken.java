package com.f1community.backend.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
public class RefreshToken {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Column(nullable = false)
    private String token;

    @Setter
    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime expiresAt;

    public RefreshToken(User user, String token) {
        this.user = user;
        this.userId = user.getId();
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusDays(7);
    }

    protected RefreshToken() {}
}