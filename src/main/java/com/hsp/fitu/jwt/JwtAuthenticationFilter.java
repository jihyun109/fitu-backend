package com.hsp.fitu.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;
    private SecretKey secretKey;

    public JwtAuthenticationFilter(RedisTemplate<String, String> redisTemplate, @Value("${jwt.secret}") String signingKey) {
        this.redisTemplate = redisTemplate;
        this.secretKey = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // 1. 특정 경로를 필터에서 넘어가게
        if (path.equals("/login") || path.equals("/signup") || path.equals("/auth/login/kakao") || path.equals("/auth/reissue") || pathMatcher.match("/swagger-ui/**", path) || pathMatcher.match("/v3/api-docs/**", path) || pathMatcher.match("/ws/**", path) || pathMatcher.match("/actuator/**", path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 토큰 추출
        String jwt = request.getHeader("Authorization");
        // 3. 토큰 형식 오류 & 블랙리스트 확인 -> 분리
        if (jwt == null || !jwt.startsWith("Bearer ") || isTokenBlacklisted(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT token missing or invalid");
            return;
        }

        // 4. 'Bearer ' 접두어를 제거하고 실제 JWT 토큰만 가져오기
        jwt = jwt.substring(7);

        // 5. 토큰 검증.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

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

    private boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist: " + token);
    }
}
