package com.f1community.backend.service.user;

import com.f1community.backend.dto.user.request.UserSignupRequestDto;
import com.f1community.backend.dto.user.request.UserUpdateRequestDto;
import com.f1community.backend.dto.user.response.LoginResponseDto;
import com.f1community.backend.dto.user.response.UserResponseDto;
import com.f1community.backend.domain.user.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    void signup(UserSignupRequestDto dto, MultipartFile profileImage);
    User authenticate(String loginId, String password);

    LoginResponseDto login(String loginId, String password);

    UserResponseDto getUserById(Long id);

    User updateProfile(Long id, UserUpdateRequestDto dto);

    void deactivateUser(Long id);
}
