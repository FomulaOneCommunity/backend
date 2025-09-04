package com.f1community.backend.user.service;

import com.f1community.backend.config.PasswordResetProperties;
import com.f1community.backend.user.dto.request.ForgotPasswordRequest;
import com.f1community.backend.user.dto.request.ResetPasswordRequest;
import com.f1community.backend.user.exception.InvalidPasswordException;
import com.f1community.backend.user.exception.InvalidResetTokenException;
import com.f1community.backend.util.EmailService;
import com.f1community.backend.util.ResetUrlBuilder;
import com.f1community.backend.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final StringRedisTemplate redis;
    private final PasswordResetProperties props;
    private final EmailService emailService;
    private final ResetUrlBuilder resetUrlBuilder;

    private final UserQueryPort userQuery;
    private final UserPasswordPort userPassword;
    private final PasswordEncoder passwordEncoder;

    /**
     * Sends a password reset email with a unique token.
     */
    public void requestReset(ForgotPasswordRequest req, String requestedLocale) {
        var user = userQuery.getByEmailOrThrow(req.getEmail());

        String token = UUID.randomUUID().toString();
        String key = props.getRedisPrefix() + token;

        redis.opsForValue().set(
                key,
                String.valueOf(user.id()),
                Duration.ofMinutes(props.getExpiryMinutes())
        );

        String userPreferredLocale = null; // Use stored locale from User entity if available
        String resetLink = resetUrlBuilder.build(token, requestedLocale, userPreferredLocale);

        emailService.sendPasswordResetEmail(
                user.email(),
                "[F1Community] Password Reset Request",
                resetLink,
                user.firstName(),
                user.lastName()
        );
    }

    /**
     * Confirms a password reset request by validating the token and updating the password.
     */
    @Transactional
    public void confirmReset(ResetPasswordRequest req) {
        // 0) Input validation
        if (req == null) throw new InvalidResetTokenException(ErrorCode.INVALID_TOKEN.getMessage());

        final String token = req.getToken() == null ? "" : req.getToken().trim();
        if (token.isEmpty()) throw new InvalidResetTokenException(ErrorCode.INVALID_TOKEN.getMessage());

        final String newPw = req.getNewPassword();
        if (newPw == null || newPw.isBlank()) throw new InvalidPasswordException(ErrorCode.INVALID_PASSWORD.getMessage());

        // 1) Retrieve token from Redis (treat Redis errors as invalid token)
        final String key = props.getRedisPrefix() + token;
        final String userIdStr;
        try {
            userIdStr = redis.opsForValue().get(key);
        } catch (DataAccessException e) {
            log.error("Failed to retrieve reset token from Redis key={}", key, e);
            throw new InvalidResetTokenException(ErrorCode.INVALID_TOKEN.getMessage());
        }
        if (userIdStr == null) {
            throw new InvalidResetTokenException(ErrorCode.INVALID_TOKEN.getMessage());
        }

        // 2) Parse token value (prevent malformed data)
        final Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new InvalidResetTokenException(ErrorCode.INVALID_TOKEN.getMessage());
        }

        // 3) Update password
        final String encoded = passwordEncoder.encode(newPw);
        userPassword.changePassword(userId, encoded);

        // 4) Delete used token (best effort)
        try {
            redis.delete(key);
        } catch (DataAccessException e) {
            log.warn("Failed to delete reset token from Redis key={}, err={}", key, e.getMessage());
        }
    }

    public interface UserQueryPort {
        UserSummary getByEmailOrThrow(String email);
    }

    public interface UserPasswordPort {
        void changePassword(Long userId, String encodedPassword);
    }

    public record UserSummary(Long id, String email, String firstName, String lastName) {}
}