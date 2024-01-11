package com.study.board.user.repository;

import com.study.board.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email); //email로 user찾기
    User findByNickname(String nickname); //nickname으로 user찾기
    User findByRefreshToken(String refreshToken); //refreshtoken으로 user찾기
    User findByProviderAndProviderId(String provider, String providerId); //소셜로그인 종류와 아이디로 user찾기
}
