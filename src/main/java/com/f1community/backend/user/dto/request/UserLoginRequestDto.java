package com.f1community.backend.user.dto.request;

import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    private String loginId;
    private String password;
}
