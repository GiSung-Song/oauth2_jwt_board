package com.study.board.post.service;

import com.study.board.user.etc.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        // 비어있는 security context 생성
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        final UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(customUser.username(), customUser.password(),
                Arrays.asList(new SimpleGrantedAuthority(Role.MEMBER.name())));

        securityContext.setAuthentication(authenticationToken);

        return securityContext;
    }
}
