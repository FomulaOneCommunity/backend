package com.f1community.backend.user.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequestDto {
    private String username;
    private String email;
    private String profileImage;
    private String country;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private String favoriteTeam;
    private String favoriteDriver;
}

