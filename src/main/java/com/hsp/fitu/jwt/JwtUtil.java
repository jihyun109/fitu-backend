package com.hsp.fitu.jwt;

import com.hsp.fitu.entity.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token.access-expiration-time}") Long access,
            @Value("${jwt.token.refresh-expiration-time}") Long refresh) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        accessExpMs = access;
        refreshExpMs = refresh;
    }

    public String createAccessToken(long userId, Role role) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessExpMs);


        // claim
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        if (role != null) {
            claims.put("role", role.toString());
        }

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .addClaims(claims)
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