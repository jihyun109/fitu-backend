package com.hsp.fitu.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

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

}
