package com.f1community.backend.admin.service;

import com.f1community.backend.user.domain.Role;
import com.f1community.backend.user.domain.User;
import com.f1community.backend.user.domain.UserStatus;
import com.f1community.backend.user.repository.UserRepository;
import com.f1community.backend.user.dto.response.UserResponseDto;
import com.f1community.backend.util.ProfileImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.f1community.backend.admin.exception.AdminUserNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    @Value("${upload.path}")
    private String uploadRootDir;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponseDto.from(
                        user,
                        ProfileImageUtil.toBase64FromWebPath(
                                user.getProfileImage(),
                                user.getUsername(),
                                uploadRootDir,
                                true
                        )
                ))
                .toList();
    }

    public void updateUserRole(Long userId, String roleStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(AdminUserNotFoundException::new);

        Role role = Role.valueOf(roleStr.toUpperCase());
        user.setRole(role);
        userRepository.save(user);
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(AdminUserNotFoundException::new);

        String base64 = ProfileImageUtil.toBase64FromWebPath(
                user.getProfileImage(),
                user.getUsername(),
                uploadRootDir,
                true
        );
        return UserResponseDto.from(user, base64);
    }

    public void updateUserStatus(Long userId, String statusStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(AdminUserNotFoundException::new);

        UserStatus status = UserStatus.valueOf(statusStr.toUpperCase());
        user.setStatus(status);
        userRepository.save(user);
    }

    public void deleteUserByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(AdminUserNotFoundException::new);
        userRepository.delete(user);
    }
}