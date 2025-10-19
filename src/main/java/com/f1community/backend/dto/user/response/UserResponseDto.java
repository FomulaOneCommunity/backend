package com.f1community.backend.dto.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.f1community.backend.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String country;
    private String profileImageBase64;

    private String favoriteTeam;
    private String favoriteDriver;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    public static UserResponseDto from(User user, String profileImageBase64) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .country(user.getCountry())
                .profileImageBase64(profileImageBase64)
                .favoriteTeam(user.getFavoriteTeam())
                .favoriteDriver(user.getFavoriteDriver())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}