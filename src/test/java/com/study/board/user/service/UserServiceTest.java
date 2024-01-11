package com.study.board.user.service;

import com.study.board.user.dto.UserSignUpDto;
import com.study.board.user.entity.User;
import com.study.board.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원가입")
    class singUp {

        UserSignUpDto createUserSignUpDto() {

            UserSignUpDto userSignUpDto = new UserSignUpDto();

            userSignUpDto.setEmail("test");
            userSignUpDto.setPassword("test");
            userSignUpDto.setNickname("test");

            return userSignUpDto;
        }

        User createUser(UserSignUpDto userSignUpDto) {
            User user = User.builder()
                    .email(userSignUpDto.getEmail())
                    .password(passwordEncoder.encode(userSignUpDto.getPassword()))
                    .nickname(userSignUpDto.getNickname())
                    .build();

            return user;
        }

        @Test
        @DisplayName("성공 케이스")
        void success() throws Exception {
            //given
            UserSignUpDto userSignUpDto = createUserSignUpDto();
            User user = createUser(userSignUpDto);

            Long mockMemberId = 1L;
            ReflectionTestUtils.setField(user, "id", mockMemberId);

            //mocking
            when(userRepository.save(any())).thenReturn(user);
            when(userRepository.findById(mockMemberId)).thenReturn(Optional.ofNullable(user));

            //when
            Long newUserId = userService.signUp(userSignUpDto);

            //then
            User findUser = userRepository.findById(newUserId).get();

            assertThat(user.getId()).isEqualTo(newUserId);
            assertThat(user.getEmail()).isEqualTo(findUser.getEmail());
        }

        @Test
        @DisplayName("실패 케이스 - 이메일 중복")
        void fail() {
            //given
            UserSignUpDto userSignUpDto = createUserSignUpDto();
            User user = createUser(userSignUpDto);
            userRepository.save(user);

            UserSignUpDto saveDto = new UserSignUpDto();
            saveDto.setEmail("test");
            saveDto.setPassword("test2");
            saveDto.setNickname("test2");

            //when, then
            Assertions.assertThrows(Exception.class, () -> {
                userService.signUp(saveDto);
            });
        }

    }

}