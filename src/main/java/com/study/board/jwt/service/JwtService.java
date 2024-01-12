package com.study.board.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.study.board.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken"; //accessToken subject
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken"; //refreshToken subject
    private static final String EMAIL_CLAIM = "email"; //email calim
    private static final String BEARER = "Bearer "; //Bearer (토큰) (Value) 형식

    private final UserRepository userRepository;

    @Value("${jwt.secretKey}")
    private String secretKey; //개인키

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod; //access token 유효시간

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod; //refresh token 유효시간

    @Value("${jwt.access.header}")
    private String accessHeader; //엑세스 토큰의 헤더 key

    @Value("${jwt.refresh.header}")
    private String refreshHeader; //리프레시 토큰의 헤더 key

    //AccessToken 생성
    public String createAccessToken(String email) {
        Date now = new Date();

        //RSA 방식(개인키, 공개키) 없음 / Hash 암호 방식
        return JWT.create() //JWT 생성 빌더
                .withSubject(ACCESS_TOKEN_SUBJECT) //JWT 제목(subject)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) //토큰 만료시간 현재시간 + 설정해둔 유효시간
                .withClaim(EMAIL_CLAIM, email) //클레임
                .sign(Algorithm.HMAC512(secretKey)); //서명 : HMAC512 알고리즘으로 암호화
    }

    //RefreshToken 생성
    public String createRefreshToken() {
        Date now = new Date();

        //RSA 방식(개인키, 공개키) 없음 / Hash 암호 방식
        return JWT.create() //JWT 생성 빌더
                .withSubject(REFRESH_TOKEN_SUBJECT) //JWT 제목(subject)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod)) //토큰 만료시간 현재시간 + 설정해둔 유효시간
                .sign(Algorithm.HMAC512(secretKey)); //서명 : HMAC512 알고리즘으로 암호화
    }

    //AccessToken을 헤더에 담기
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        log.info("AccessToken : {}", accessToken);

        response.setStatus(HttpServletResponse.SC_OK); //200 성공
        response.setHeader(accessHeader, accessToken); //Authorization : accessToken
    }

    //AccessToken + RefreshToken 헤더에 담기
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        log.info("AccessToken : {}", accessToken);
        log.info("RefreshToken : {}", refreshToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken); //Authorization : accessToken
        response.setHeader(refreshHeader, refreshToken); //Authorization-refresh : refreshToken
    }

    //refresh 토큰 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER)) //'Barer '로 시작하는 리프레시 토큰을 찾아서
                .map(refreshToken -> refreshToken.replace(BEARER, "")); //'Bearer ' -> ''로 치환하여 가져오기
    }

    //access 토큰 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER)) //'Barer '로 시작하는 액세스 토큰을 찾아서
                .map(accessToken -> accessToken.replace(BEARER, "")); //'Bearer ' -> ''로 치환하여 가져오기
    }

    //access 토큰에서 email 추출
    public Optional<String> extractEmail(String accessToken) {
        try {
            // 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 반환
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(EMAIL_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    //refresh 토큰 DB 저장
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }

    //토큰 유효성 검사
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);

            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());

            return false;
        }
    }
}
