package com.hsp.fitu.jwt;

import com.hsp.fitu.config.SecurityConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenService.extractTokenFromHeader(request);
        Claims claims = jwtTokenService.validateToken(token);

        // 6. 클레임 파싱
        Long userId = claims.get("userId", Long.class);
        String role = (String) claims.get("role");
        Number universityIdNumber = (Number) claims.get("universityId");
        Long universityId = universityIdNumber != null ? universityIdNumber.longValue() : null;

        // 7. SecurityContext 에 추가할 Authentication 인스턴스 생성
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null && !role.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        // 8. 인증 객체 생성
        CustomUserDetails userDetails = new CustomUserDetails(userId, claims.getSubject(), authorities, universityId);

        UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(userDetails, null, authorities);

        // 9. SecurityContext에 Authentication 객체 추가
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        filterChain.doFilter(request, response);    // 필터 체인의 다음 필터 호룿
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return SecurityConstants.PERMIT_ALL_MATCHERS.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }
}
