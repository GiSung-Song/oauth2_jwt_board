package com.study.board.oauth2.service;

import com.study.board.oauth2.CustomOAuth2User;
import com.study.board.oauth2.OAuthAttributes;
import com.study.board.oauth2.provider.OAuth2UserInfo;
import com.study.board.user.entity.User;
import com.study.board.user.etc.Role;
import com.study.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        /**
         * DefaultOAuth2UserService는 OAuth2UserService의 구현체
         * DefaultOAuth2UserService loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청
         * 사용자 정보를 얻은 후, DefaultOAuth2User 객체를 생성 후 반환
         * OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 객체
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 소셜 로그인 종류 - ex) kakao, google, naver
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 시 키(PK)가 되는 값
        String userNameAttributeName = userRequest
                .getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 API가 제공하는 userInfo의 json 값(유저 정보들)

        //소셜 로그인 종류에 따른 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);
        User user = getUser(extractAttributes);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                user.getEmail(),
                user.getRole()
        );
    }

    private User getUser(OAuthAttributes authAttributes) {
        String provider = authAttributes.getOAuth2UserInfo().getProvider();
        String providerId = provider + "_" + authAttributes.getOAuth2UserInfo().getProviderId();

        User userEntity = userRepository.findByProviderAndProviderId(provider, providerId).orElse(null);

        if (userEntity == null) {

            OAuth2UserInfo userInfo = authAttributes.getOAuth2UserInfo();

            userEntity = User.builder()
                    .username(userInfo.getName())
                    .email(userInfo.getEmail())
                    .provider(provider)
                    .providerId(providerId)
                    .role(Role.MEMBER)
                    .nickname(UUID.randomUUID().toString())
                    .build();

            userRepository.save(userEntity);
        }

        return userEntity;
    }
}
