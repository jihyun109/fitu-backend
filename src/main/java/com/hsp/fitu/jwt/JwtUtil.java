package com.hsp.fitu.jwt;

import com.hsp.fitu.entity.enums.Role;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final StringRedisTemplate redisTemplate;

    public JwtUtil(
            @Value("${jwt.access-secret}") String accessSecret,
            @Value("${jwt.refresh-secret}") String refreshSecret,
            @Value("${jwt.token.access-expiration-time}") Long access,
            @Value("${jwt.token.refresh-expiration-time}") Long refresh,
            StringRedisTemplate redisTemplate) {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMs = access;
        this.refreshExpMs = refresh;
        this.redisTemplate = redisTemplate;
    }

    public String createAccessToken(long userId, Role role, Long universityId) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessExpMs);

        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("universityId", universityId);
        claims.put("type", "ACCESS");
        if (role != null) {
            claims.put("role", role.toString());
        }

        return "Bearer " + Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(accessKey)
                .compact();
    }

    public String createRefreshToken(long userId, String familyId, String jti) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(refreshExpMs);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setId(jti)
                .addClaims(Map.of(
                        "userId", userId,
                        "type", "REFRESH",
                        "fid", familyId
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(refreshKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims validateAndGetClaims(String token) throws Exception {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.JWT_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new BusinessException(ErrorCode.INVALID_JWT);
        } catch (SignatureException e) {
            throw new BusinessException(ErrorCode.JWT_SIGNATURE_INVALID);
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(ErrorCode.JWT_UNSUPPORTED);
        } catch (Exception e) {
            log.error("JWT parsing error", e);
            throw new BusinessException(ErrorCode.INTER_SERVER_ERROR);
        }
    }

    public Claims parseRefreshClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String type = claims.get("type", String.class);
            if (!"REFRESH".equals(type)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }
            return claims;
        } catch (BusinessException e) {
            throw e;
        } catch (JwtException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    public Long getUserId(Claims claims) {
        return claims.get("userId", Long.class);
    }

    public String getJti(Claims claims) {
        return claims.getId();
    }

    public String getFamilyId(Claims claims) {
        return claims.get("fid", String.class);
    }

    // token 무효화 (AT 블랙리스트용)
    public void invalidateToken(String token, String value) {
        long expiration = getExpiration(token.substring(7)).getTime();
        redisTemplate.opsForValue().set("blacklist: " + token, value, Duration.ofMillis(expiration));
    }

    public boolean isExpired(long tokenExpiryMillis) {
        return System.currentTimeMillis() > tokenExpiryMillis;
    }

    public long getExpiryMillis(Claims claims) {
        return claims.getExpiration().getTime();
    }

    private Date getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
}