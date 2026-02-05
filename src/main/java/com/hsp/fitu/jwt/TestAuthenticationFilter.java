package com.hsp.fitu.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class TestAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 Test User ID 추출
        String testUserId = request.getHeader("X-Test-User-Id");

        // 2. 헤더가 존재하면 강제 인증 처리
        if (testUserId != null) {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    Long.valueOf(testUserId), // Principal
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 권한
            );

            // SecurityContext에 인증 정보 주입
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 3. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}