package com.f1community.backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private UserResponseDto user;
    private String accessToken;
    private String refreshToken;
}
