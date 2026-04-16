package com.hsp.fitu.jwt;

import com.hsp.fitu.config.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final HandlerExceptionResolver exceptionResolver;
    private final RequestMatcher permitAllMatcher = new OrRequestMatcher(SecurityConstants.PERMIT_ALL_MATCHERS);

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtTokenService = jwtTokenService;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return permitAllMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtTokenService.extractTokenFromHeader(request);

            // 토큰이 있을 때만 인증 시도
            if (token != null) {
                Claims claims = jwtTokenService.validateToken(token);

                // RT가 AT로 사용되는 것을 방지 (키 분리 + type 이중 방어)
                String type = claims.get("type", String.class);
                if ("REFRESH".equals(type)) {
                    throw new JwtException("Refresh token cannot be used as access token");
                }

                // SecurityContext 에 Authentication 인스턴스 추가
                Authentication auth = jwtTokenService.createAuthentication(claims);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }
}
