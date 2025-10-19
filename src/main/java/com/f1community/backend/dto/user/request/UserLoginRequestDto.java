package com.f1community.backend.dto.user.request;

import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    private String loginId;
    private String password;
}
