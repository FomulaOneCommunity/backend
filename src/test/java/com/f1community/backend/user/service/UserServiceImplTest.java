package com.f1community.backend.user.service;

import com.f1community.backend.domain.user.User;
import com.f1community.backend.dto.user.request.UserSignupRequestDto;
import com.f1community.backend.domain.user.UserRepository;
import com.f1community.backend.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void signUpSuccess() {
        // given
        UserSignupRequestDto dto = UserSignupRequestDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("SecurePass123!")
                .country("KR")
                .firstName("Minseok")
                .lastName("Choi")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        MockMultipartFile mockImage = new MockMultipartFile(
                "profileImage",
                "test.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        // when
        userService.signup(dto, mockImage);

        // then
        User saved = userRepository.findByEmail("test@example.com").orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("testuser");
        assertThat(saved.getFirstName()).isEqualTo("Minseok");
    }
}
