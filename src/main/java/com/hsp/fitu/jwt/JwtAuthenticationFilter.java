package com.hsp.fitu.jwt;

import com.hsp.fitu.config.SecurityConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenService.extractTokenFromHeader(request);
        Claims claims = jwtTokenService.validateToken(token);

        // SecurityContext 에 Authentication 인스턴스 추가
        SecurityContextHolder.getContext()
                .setAuthentication(jwtTokenService.createAuthentication(claims));

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return SecurityConstants.PERMIT_ALL_MATCHERS.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }
}
