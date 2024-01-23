package com.study.board.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.board.jwt.filter.JwtAuthenticationProcessingFilter;
import com.study.board.jwt.service.JwtService;
import com.study.board.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.study.board.login.handler.LoginFailureHandler;
import com.study.board.login.handler.LoginSuccessHandler;
import com.study.board.login.service.LoginService;
import com.study.board.oauth2.handler.OAuth2LoginFailureHandler;
import com.study.board.oauth2.handler.OAuth2LoginSuccessHandler;
import com.study.board.oauth2.service.CustomOAuth2UserService;
import com.study.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    // 필터를 타지 않고 허용되는 uri
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring().requestMatchers(HttpMethod.GET, "/post");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class)
                .csrf(CsrfConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .headers(config ->
                        config.frameOptions(opt -> opt.disable()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(auth -> auth
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(service -> service
                                .userService(customOAuth2UserService)))
                .authorizeHttpRequests((authZ) -> authZ
                        .requestMatchers("/member/**").authenticated()
                        .requestMatchers("/post/**").hasAnyRole("MANAGER", "ADMIN", "MEMBER")
                        .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/post").permitAll()
                        .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new CustomBCryptPasswordEncoder());
        provider.setUserDetailsService(loginService);

        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, userRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter
                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);

        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());

        return customJsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(jwtService, userRepository);

        return jwtAuthenticationProcessingFilter;
    }

}
