package com.study.board.jwt.filter;

import com.study.board.jwt.service.JwtService;
import com.study.board.user.entity.User;
import com.study.board.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; //login으로 들어오는 요청은 Filter 적용 X

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // /login 요청이 오면
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            //다음 filter 실행
            filterChain.doFilter(request, response);
            return; //이후 필터 진행 막기
        }

        // 사용자 요청에서 refreshToken 호출
        // refreshToken이 없거나 유효하지 않다면 null 반환
        // refreshToken이 있는 경우는 AccessToken이 만료된 경우
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        // 리프레시 토큰이 header에 담겨져 있다면, DB의 리프레시 토큰과 비교하여 맞으면 AccessToken 재발급
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);

            return; //refresh 토큰과 Access 토큰을 재발급 해주고 인증 처리는 하지 않음.
        }

        if (refreshToken == null) {

        }
    }

    // 리프레시 토큰으로 user를 찾고 refreshToken 재발급 후
    // 리프레시 토큰과 액세스 토큰을 헤더에 담기
    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(user);
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()), reIssuedRefreshToken);
                });
    }

    // 리프레시 토큰을 재발급하고 user db에 refresh 토큰 저장
    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken(); //refreshToken 생성
        user.updateRefreshToken(reIssuedRefreshToken); //user에 새로운 refreshToken 저장
        userRepository.saveAndFlush(user); //user db에 user 저장

        return reIssuedRefreshToken;
    }

    // 액세스 토큰 검증 및 인증 처리
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                   FilterChain filterChain) throws ServletException, IOException {

        jwtService.extractAccessToken(request) //AccessToken 추출
                .filter(jwtService::isTokenValid) //토큰 유효성 검사
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken) //토큰으로 email 추출
                        .ifPresent(email -> userRepository.findByEmail(email) // user db에서 email로 등록된 회원 찾기
                                .ifPresent(this::saveAuthentication))); //인증 처리

        filterChain.doFilter(request, response);
    }

    //인증 정보 저장
    private void saveAuthentication(User user) {
        String password = user.getPassword();

        if (password == null) { //소셜 로그인 유저의 비밀번호를 임의로 설정하여 소셜 로그인 유저도 인증 되도록 설정
            password = randomPassword();
        }

        // user 정보를 builder 패턴을 이용하여 UserDetails 객체 생성
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(password)
                .roles(user.getRole().name())
                .build();

        //인증 정보를 담고
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        //시큐리티 컨텍스트에서 인증 정보를 추가
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    //랜덤 패스워드 설정
    private String randomPassword() {
        char[] tmp = new char[10];

        for (int i = 0; i < tmp.length; i++) {
            int div = (int) Math.floor(Math.random() * 2);

            if (div == 0) {
                tmp[i] = (char) (Math.random() * 10 + '0'); //숫자
            } else {
                tmp[i] = (char) (Math.random() * 26 + 'A'); //영문 대문자
            }
        }

        return new String(tmp);
    }
}
