package com.hsp.fitu.jwt;

import com.hsp.fitu.entity.enums.Role;
import com.hsp.fitu.error.customExceptions.JwtExpiredException;
import com.hsp.fitu.error.customExceptions.JwtInvalidException;
import com.hsp.fitu.error.customExceptions.JwtSignatureInvalidException;
import com.hsp.fitu.error.customExceptions.JwtUnsupportedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final StringRedisTemplate redisTemplate;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token.access-expiration-time}") Long access,
            @Value("${jwt.token.refresh-expiration-time}") Long refresh, StringRedisTemplate redisTemplate) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        accessExpMs = access;
        refreshExpMs = refresh;
        this.redisTemplate = redisTemplate;
    }

    public String createAccessToken(long userId, Role role, Long universityId) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessExpMs);

        // claim
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("universityId", universityId);
        if (role != null) {
            claims.put("role", role.toString());
        }

        return "Bearer " + Jwts.builder()
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

    // 2) 토큰 유효성 검증 + Claims 반환
    public Claims validateAndGetClaims(String token) throws Exception {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (MalformedJwtException e) {
            throw new JwtInvalidException();
        } catch (JwtSignatureInvalidException e) {
            throw new JwtSignatureInvalidException();
        } catch (JwtUnsupportedException e) {
            throw new JwtUnsupportedException();
        } catch (Exception e) {
            throw new Exception();
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

    // token 무효화
    public void invalidateToken(String token, String value) {
        long expiration = getExpiration(token.substring(7)).getTime();
        redisTemplate.opsForValue().set("blacklist: " + token, value, Duration.ofMillis(expiration));
    }

    // jwt의 만료시간 get
    private Date getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
}