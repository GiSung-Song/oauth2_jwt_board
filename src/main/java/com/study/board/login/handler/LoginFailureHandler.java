package com.study.board.login.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400 ERROR
        response.setCharacterEncoding("UTF-8"); //UTF-8 Encoding
        response.setContentType("text/plain;charset=UTF-8"); //content-type
        response.getWriter().write("로그인 실패!!!\n");
        response.getWriter().write("이메일이나 비밀번호를 확인해주세요.");

        log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());

        super.onAuthenticationFailure(request, response, exception);
    }
}
