package com.hsp.fitu.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist: " + token);
    }
}
