package com.hsp.fitu.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final TokenBlackListService tokenBlackListService;
    private final SecretKey secretKey;

    public String extractTokenFromHeader(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");

        if (jwt == null || !jwt.startsWith("Bearer ") || tokenBlackListService.isTokenBlacklisted(jwt)) {
            throw new JwtException("JWT token missing or invalid");
        }

        // 'Bearer ' 접두어를 제거하고 실제 JWT 토큰만 반환
        return jwt.substring(7);
    }

    public Claims validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication createAuthentication(Claims claims) {
        Long userId = claims.get("userId", Long.class);
        if (userId == null) {
            throw new JwtException("JWT token missing required claim: userId");
        }

        String role = (String) claims.get("role");
        Long universityId = claims.get("universityId", Long.class);

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null && !role.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        CustomUserDetails userDetails = new CustomUserDetails(userId, claims.getSubject(), authorities, universityId);

        return new UsernamePasswordAuthentication(userDetails, null, authorities);
    }

}
