package com.f1community.backend.service.user;

import com.f1community.backend.common.exception.User.*;
import com.f1community.backend.dto.user.request.UserSignupRequestDto;
import com.f1community.backend.dto.user.request.UserUpdateRequestDto;
import com.f1community.backend.dto.user.response.LoginResponseDto;
import com.f1community.backend.dto.user.response.UserResponseDto;
import com.f1community.backend.domain.user.*;
import com.f1community.backend.util.*;
import com.f1community.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${upload.path}")
    private String uploadRootDir;

    @Value("${default.profile-image}")
    private String defaultProfileImagePath;

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String normalizeName(String input) {
        if (input == null || input.isEmpty()) return input;

        String lower = input.toLowerCase(Locale.ROOT);
        return lower.substring(0, 1).toUpperCase(Locale.ROOT) + lower.substring(1);
    }

    private void validatePassword(String password, UserSignupRequestDto dto) {
        if (password.length() < 12) {
            throw new InvalidPasswordException(ErrorCode.PASSWORD_TOO_SHORT.format());
        }

        if (!password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*\\d.*") ||
                !password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:'\",.<>/?`~].*")) {
            throw new InvalidPasswordException(ErrorCode.PASSWORD_MISSING_REQUIREMENTS.format());
        }

        String lowerPassword = password.toLowerCase();
        List<String> forbiddenWords = Arrays.asList("password", "1234", "qwerty", "letmein", "welcome");

        for (String word : forbiddenWords) {
            if (lowerPassword.contains(word)) {
                throw new InvalidPasswordException(ErrorCode.PASSWORD_CONTAINS_FORBIDDEN_WORD.format(word));
            }
        }

        if (lowerPassword.contains(dto.getUsername().toLowerCase()) ||
                lowerPassword.contains(dto.getFirstName().toLowerCase()) ||
                lowerPassword.contains(dto.getLastName().toLowerCase()) ||
                lowerPassword.contains(dto.getEmail().split("@")[0].toLowerCase()) ||
                (dto.getBirthDate() != null &&
                        lowerPassword.contains(dto.getBirthDate().toString().replace("-", "")))) {
            throw new InvalidPasswordException(ErrorCode.PASSWORD_CONTAINS_USER_INFO.format());
        }
    }

    @Override
    public void signup(UserSignupRequestDto dto, MultipartFile profileImage) {
        // 1. Email duplication check
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }

        // 2. Username duplication check
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }

        // 3. Password validation
        String password = dto.getPassword();
        validatePassword(password, dto);

        // 4. Encode password
        String encodedPassword = passwordEncoder.encode(password);

        // 5. Determine timezone
        String timezone = TimeZoneUtil.getTimeZoneByCountryCode(dto.getCountry());

        // 6. Save profile image (if provided)
        String imagePath;
        if (profileImage != null && !profileImage.isEmpty()) {
            String username = dto.getUsername();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String originalFilename = profileImage.getOriginalFilename();
            assert originalFilename != null;
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String storedFileName = username + "_" + timestamp + extension;

            Path userDirPath = Paths.get(uploadRootDir, username);
            try {
                Files.createDirectories(userDirPath);
            } catch (IOException e) {
                throw new ImageUploadException(ErrorCode.PROFILE_IMAGE_DIR_CREATE_FAILED, e);
            }

            File dest = userDirPath.resolve(storedFileName).toFile();
            try {
                profileImage.transferTo(dest);
                Path relativePath = Paths.get("uploads", "profile-images", username, storedFileName);
                imagePath = File.separator + relativePath.toString().replace(File.separatorChar, '/');
            } catch (IOException e) {
                throw new ImageUploadException(ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED, e);
            }
        } else {
            imagePath = defaultProfileImagePath;
        }

        // 7. Create user entity
        String formattedFirstName = normalizeName(dto.getFirstName());
        String formattedLastName = normalizeName(dto.getLastName());

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encodedPassword)
                .profileImage(imagePath)
                .country(dto.getCountry())
                .timezone(timezone)
                .firstName(formattedFirstName)
                .lastName(formattedLastName)
                .birthDate(dto.getBirthDate())
                .favoriteTeam(dto.getFavoriteTeam())
                .favoriteDriver(dto.getFavoriteDriver())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        // 8. Save to DB
        userRepository.save(user);
    }

    @Override
    public User authenticate(String loginId, String password) {
        User user = userRepository.findByUsername(loginId)
                .or(() -> userRepository.findByEmail(loginId))
                .orElseThrow(() -> new UserNotFoundException(loginId));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException(ErrorCode.INCORRECT_PASSWORD.getMessage());
        }

        return user;
    }

    @Override
    public LoginResponseDto login(String loginId, String password) {
        User user = userRepository.findByUsername(loginId)
                .or(() -> userRepository.findByEmail(loginId))
                .orElseThrow(() -> new UserNotFoundException(loginId));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException(ErrorCode.INCORRECT_PASSWORD.getMessage());
        }

        String profileImageBase64 = ProfileImageUtil.toBase64FromWebPath(
                user.getProfileImage(),
                user.getUsername(),
                uploadRootDir,
                true
        );

        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        RefreshToken refreshTokenEntity = refreshTokenRepository.findById(user.getId())
                .orElse(new RefreshToken(user, refreshToken));
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponseDto(
                UserResponseDto.from(user, profileImageBase64),
                accessToken,
                refreshToken
        );
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    String profileImageBase64 = ProfileImageUtil.toBase64FromWebPath(
                            user.getProfileImage(),
                            user.getUsername(),
                            uploadRootDir,
                            true
                    );
                    return UserResponseDto.from(user, profileImageBase64);
                })
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }

    @Override
    @Transactional
    public User updateProfile(Long id, UserUpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));

        if (UserUpdateUtils.isValid(dto.getUsername())) user.setUsername(dto.getUsername());
        if (UserUpdateUtils.isValid(dto.getEmail())) user.setEmail(dto.getEmail());
        if (UserUpdateUtils.isValid(dto.getCountry())) user.setCountry(dto.getCountry());
        if (UserUpdateUtils.isValid(dto.getFirstName())) user.setFirstName(dto.getFirstName());
        if (UserUpdateUtils.isValid(dto.getLastName())) user.setLastName(dto.getLastName());
        if (UserUpdateUtils.isValid(dto.getFavoriteTeam())) user.setFavoriteTeam(dto.getFavoriteTeam());
        if (UserUpdateUtils.isValid(dto.getFavoriteDriver())) user.setFavoriteDriver(dto.getFavoriteDriver());

        if (dto.getBirthDate() != null && !"string".equals(dto.getBirthDate().toString())) {
            user.setBirthDate(dto.getBirthDate());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));

        user.setStatus(UserStatus.DEACTIVATED);
        user.setDeactivatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}