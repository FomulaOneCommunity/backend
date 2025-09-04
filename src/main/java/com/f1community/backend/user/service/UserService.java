package com.f1community.backend.user.service;

import com.f1community.backend.user.domain.User;
import com.f1community.backend.user.dto.request.*;
import com.f1community.backend.user.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    void signup(UserSignupRequestDto dto, MultipartFile profileImage);
    User authenticate(String loginId, String password);

    LoginResponseDto login(String loginId, String password);

    UserResponseDto getUserById(Long id);

    User updateProfile(Long id, UserUpdateRequestDto dto);

    void deactivateUser(Long id);
}
