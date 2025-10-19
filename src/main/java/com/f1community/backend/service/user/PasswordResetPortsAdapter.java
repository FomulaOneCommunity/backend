package com.f1community.backend.service.user;

import com.f1community.backend.domain.user.User;
import com.f1community.backend.domain.user.UserRepository;
import com.f1community.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetPortsAdapter
        implements PasswordResetService.UserQueryPort,
        PasswordResetService.UserPasswordPort {

    private final UserRepository userRepository;

    @Override
    public PasswordResetService.UserSummary getByEmailOrThrow(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage() + " (email: " + email + ")"));
        return new PasswordResetService.UserSummary(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName()
        );
    }

    @Override
    public void changePassword(Long userId, String encodedPassword) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage() + " (id: " + userId + ")"));
        u.setPassword(encodedPassword);
        userRepository.save(u);
    }
}