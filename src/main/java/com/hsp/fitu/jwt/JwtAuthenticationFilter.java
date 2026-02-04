package com.hsp.fitu.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final HandlerExceptionResolver exceptionResolver;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtTokenService = jwtTokenService;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtTokenService.extractTokenFromHeader(request);

            // 토큰이 있을 때만 인증 시도
            if (token != null) {
                Claims claims = jwtTokenService.validateToken(token);

                // SecurityContext 에 Authentication 인스턴스 추가
                Authentication auth = jwtTokenService.createAuthentication(claims);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }
}
