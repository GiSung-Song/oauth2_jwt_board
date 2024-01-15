package com.study.board.oauth2.provider;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getProviderId(); // 소셜로그인 ID (PK)
    public abstract String getProvider(); // 소셜 로그인 종류
    public abstract String getEmail(); // 이메일
    public abstract String getName(); // 이름

}
