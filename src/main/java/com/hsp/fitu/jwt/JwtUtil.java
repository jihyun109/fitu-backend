package com.hsp.fitu.jwt;

import com.hsp.fitu.entity.enums.Role;
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

    public JwtUtil(
            // 해당 @Value 값들은 yml에서 설정할 수 있다
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token.access-expiration-time}") Long access) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        accessExpMs = access;
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
}