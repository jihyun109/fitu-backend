package com.hsp.fitu.jwt;

import com.hsp.fitu.config.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
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

            if (token != null) {
                Claims claims = jwtTokenService.validateToken(token);

                // SecurityContext 에 Authentication 인스턴스 추가
                SecurityContextHolder.getContext()
                        .setAuthentication(jwtTokenService.createAuthentication(claims));
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException e) {
            // [핵심] 로그를 찍지 않고 바로 Resolver에게 배달합니다.
            exceptionResolver.resolveException(request, response, null, e);
        } catch (Exception e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return SecurityConstants.PERMIT_ALL_MATCHERS.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }
}
