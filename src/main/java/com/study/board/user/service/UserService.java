package com.study.board.user.service;

import com.study.board.user.dto.UserSignUpDto;
import com.study.board.user.entity.User;
import com.study.board.user.etc.Role;
import com.study.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //자체 회원가입 -> 소셜 로그인이 아닌 경우
    @Transactional
    public Long signUp(UserSignUpDto userSignUpDto) throws Exception {

        if (userRepository.findByEmail(userSignUpDto.getEmail()) != null) {
            throw new Exception("해당 이메일로 가입된 회원이 있습니다."); //수정 필요 : entity 이미 존재
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()) != null) {
            throw new Exception("해당 닉네임으로 가입된 회원이 있습니다."); //수정 필요 : entity 이미 존재
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(passwordEncoder.encode(userSignUpDto.getPassword())) //비밀번호를 암호화한 뒤 저장
                .nickname(userSignUpDto.getNickname())
                .role(Role.MEMBER)
                .build();

        return userRepository.save(user).getId();
    }

}