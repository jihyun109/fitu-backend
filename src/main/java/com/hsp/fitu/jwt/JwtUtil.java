package com.hsp.fitu.jwt;

import com.hsp.fitu.entity.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;

    public JwtUtil(
            // 해당 @Value 값들은 yml에서 설정할 수 있다
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token.access-expiration-time}") Long access,
            @Value("${jwt.token.refresh-expiration-time}") Long refresh) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        accessExpMs = access;
        refreshExpMs = refresh;
    }

    public String createAccessToken(long userId, Role role) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessExpMs); // 예: 30분

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .addClaims(Map.of("userId", userId,
                        "role", role.toString()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(long userId) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(refreshExpMs);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .addClaims(Map.of("userId", userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }
}