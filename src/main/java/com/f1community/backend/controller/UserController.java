package com.f1community.backend.controller;

import com.f1community.backend.common.api.ApiMessage;
import com.f1community.backend.dto.user.request.*;
import com.f1community.backend.dto.user.response.LoginResponseDto;
import com.f1community.backend.dto.user.response.UserResponseDto;
import com.f1community.backend.domain.user.User;
import com.f1community.backend.service.user.PasswordResetService;
import com.f1community.backend.service.user.UserService;
import com.f1community.backend.util.JwtUtil;
import com.f1community.backend.service.user.BlacklistTokenService;
import com.f1community.backend.domain.user.RefreshTokenRepository;
import com.f1community.backend.util.ProfileImageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Users", description = "Users-related api")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BlacklistTokenService blacklistTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final String uploadRootDir;

    private final PasswordResetService passwordResetService;

    public UserController(
            UserService userService,
            JwtUtil jwtUtil,
            BlacklistTokenService blacklistTokenService,
            RefreshTokenRepository refreshTokenRepository,
            PasswordResetService passwordResetService,
            @Value("${upload.path}") String uploadRootDir
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.blacklistTokenService = blacklistTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.uploadRootDir = uploadRootDir;
        this.passwordResetService = passwordResetService;
    }

    // 1) Sign up
    @PostMapping(
            value = "/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Sign up (multipart)", description = "Form fields + optional profile image (multipart/form-data)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sign-up completed"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiMessage> signup(@Valid @ModelAttribute UserSignupRequestDto requestDto) {
        userService.signup(requestDto, requestDto.getProfileImage());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiMessage("Sign-up completed"));
    }

    // 2) Login
    @Operation(
            summary = "Login",
            description = "Authenticates the user with the provided credentials and returns user info and tokens."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        User user = userService.authenticate(requestDto.getLoginId(), requestDto.getPassword());

        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        String base64 = ProfileImageUtil.toBase64FromWebPath(
                user.getProfileImage(),
                user.getUsername(),
                uploadRootDir,
                true
        );

        LoginResponseDto responseDto = new LoginResponseDto(
                UserResponseDto.from(user, base64),
                accessToken,
                refreshToken
        );

        return ResponseEntity.ok(responseDto);
    }

    // 3) Logout
    @Operation(
            summary = "Logout",
            description = "Adds the current token to a blacklist so it cannot be used anymore.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = jwtUtil.resolveToken(request);

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing or malformed Authorization header.");
        }
        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token.");
        }

        Long userId = jwtUtil.getUserId(accessToken);

        // Remove any stored refresh token for the user
        refreshTokenRepository.deleteByUserId(userId);

        long expiration = jwtUtil.getExpiration(accessToken);
        blacklistTokenService.addToBlacklist(accessToken, expiration);

        return ResponseEntity.ok("Logout successful");
    }

    // 4) Get a single user
    @Operation(summary = "Get user", description = "Retrieves user information by user ID.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 5) Update user information
    @Operation(summary = "Update user", description = "Updates user information by user ID.")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequestDto dto
    ) {
        User updated = userService.updateProfile(id, dto);
        String base64 = ProfileImageUtil.toBase64FromWebPath(
                updated.getProfileImage(),
                updated.getUsername(),
                uploadRootDir,
                true
        );
        return ResponseEntity.ok(UserResponseDto.from(updated, base64));
    }

    // 6) Password reset request (email → issue/send token)
    @Operation(
            summary = "Request password reset",
            description = "Issues/sends a reset token via email. (user existence is not revealed in the response.)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(schema = @Schema(implementation = ApiMessage.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/password/reset-request")
    public ResponseEntity<ApiMessage> requestPasswordReset(
            @Valid @RequestBody ForgotPasswordRequest dto,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        String locale = parseAcceptLanguage(acceptLanguage); // may be null
        passwordResetService.requestReset(dto, locale);
        return ResponseEntity.ok(
                new ApiMessage("A password reset email has been sent (same response regardless of account existence).")
        );
    }

    // 7) Password reset confirm (token + new password)
    @Operation(
            summary = "Reset password",
            description = "Validates the token in the request body and updates the password."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(schema = @Schema(implementation = ApiMessage.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/password/reset-confirm")
    public ResponseEntity<ApiMessage> confirmPasswordReset(
            @Valid @RequestBody ResetPasswordRequest dto
    ) {
        passwordResetService.confirmReset(dto);
        return ResponseEntity.ok(new ApiMessage("Password has been changed successfully."));
    }

    private String parseAcceptLanguage(String header) {
        if (header == null || header.isBlank()) return null;
        // e.g., "ko-KR,ko;q=0.9,en;q=0.8" → "ko"
        String lang = header.split(",")[0].trim();
        int dash = lang.indexOf('-');
        return dash > 0 ? lang.substring(0, dash) : lang;
    }

    // 8) Deactivate (account deletion)
    @Operation(summary = "Deactivate user", description = "Deactivates the user account by user ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("Account deactivation completed");
    }
}