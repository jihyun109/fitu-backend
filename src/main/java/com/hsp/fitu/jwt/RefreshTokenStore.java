package com.hsp.fitu.jwt;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

/**
 * Refresh Token 화이트리스트 저장소 (Redis 기반).
 *
 * - 발급된 RT를 Redis에 등록하고, 재발급(rotation) 시 원자적으로 소비(consume)한다.
 * - Reuse detection: 이미 소비된 토큰이 다시 사용되면 REUSE_DETECTED를 반환한다.
 * - Family revocation: 토큰 가족 단위로 일괄 무효화한다.
 * - Redis 장애 시 SERVICE_UNAVAILABLE 예외를 던진다 (fail-closed-but-soft).
 */
@Slf4j
@Service
public class RefreshTokenStore {

    private final StringRedisTemplate redisTemplate;
    private DefaultRedisScript<String> consumeScript;

    // Redis key prefix: 개별 토큰 저장용
    private static final String KEY_PREFIX = "rt:jti:";
    // Redis key prefix: 가족 revoke 마커용
    private static final String FAMILY_REVOKED_PREFIX = "rt:family:revoked:";

    public RefreshTokenStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Lua 스크립트 초기화.
     * Redis 서버에서 원자적으로 실행되어 동시 요청 간 경쟁 상태를 방지한다.
     *
     * 스크립트 파라미터 매핑:
     *   KEYS[1] = rt:jti:{oldJti}              — 소비할 토큰의 Redis key
     *   KEYS[2] = rt:family:revoked:{familyId}  — 가족 revoke 마커 key
     *   ARGV[1] = newJti                        — 새로 발급할 토큰의 ID
     *   ARGV[2] = ttlMillis                     — TTL (밀리초)
     *
     * 반환값: OK | FAMILY_REVOKED | NOT_FOUND | REUSE_DETECTED
     */
    @PostConstruct
    public void init() {
        consumeScript = new DefaultRedisScript<>();
        consumeScript.setResultType(String.class);
        consumeScript.setScriptText(
                // 1. 토큰 가족이 이미 revoke되었으면 즉시 거부
                "if redis.call('EXISTS', KEYS[2]) == 1 then return 'FAMILY_REVOKED' end " +
                // 2. 토큰 정보 조회
                "local v = redis.call('GET', KEYS[1]) " +
                // 3. 토큰이 Redis에 없으면 알 수 없는 토큰
                "if not v then return 'NOT_FOUND' end " +
                // 4. 이미 CONSUMED 상태면 재사용 감지 (탈취 의심)
                "if string.find(v, ':CONSUMED:') then return 'REUSE_DETECTED' end " +
                // 5. ACTIVE → CONSUMED:{newJti}로 상태 변경, TTL 유지
                "local newVal = string.gsub(v, ':ACTIVE', '') .. ':CONSUMED:' .. ARGV[1] " +
                "redis.call('SET', KEYS[1], newVal, 'PX', tonumber(ARGV[2])) " +
                // 6. 성공
                "return 'OK'"
        );
    }

    /**
     * 새 refresh token을 Redis에 등록한다.
     * 값 형식: "{userId}:{familyId}:ACTIVE"
     */
    public void issue(long userId, String familyId, String jti, long ttlMs) {
        try {
            String key = KEY_PREFIX + jti;
            String value = userId + ":" + familyId + ":ACTIVE";
            redisTemplate.opsForValue().set(key, value, Duration.ofMillis(ttlMs));
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed during refresh token issue", e);
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * 기존 refresh token을 원자적으로 소비하고, 결과를 반환한다.
     *
     * - OK: 정상 소비됨. 이후 새 토큰을 issue()로 등록해야 함.
     * - NOT_FOUND: 토큰이 Redis에 없음 (만료/위조/알 수 없음).
     * - REUSE_DETECTED: 이미 소비된 토큰이 다시 사용됨 → 탈취 의심. 호출자가 revokeFamily()를 호출해야 함.
     * - FAMILY_REVOKED: 이 토큰의 가족이 이미 무효화됨.
     *
     * Lua 스크립트로 실행되므로 동시에 같은 토큰으로 2개 요청이 와도
     * 정확히 하나만 OK, 나머지는 REUSE_DETECTED를 반환한다.
     */
    public ConsumeOutcome consume(String oldJti, String familyId, String newJti, long ttlMs) {
        try {
            // KEYS: [rt:jti:{oldJti}, rt:family:revoked:{familyId}]
            // ARGV: [newJti, ttlMs]
            String result = redisTemplate.execute(
                    consumeScript,
                    List.of(KEY_PREFIX + oldJti, FAMILY_REVOKED_PREFIX + familyId),
                    newJti,
                    String.valueOf(ttlMs)
            );
            return ConsumeOutcome.valueOf(result);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed during refresh token consume", e);
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * 토큰 가족 전체를 무효화한다.
     * 이후 같은 familyId를 가진 모든 토큰의 consume/issue가 거부된다.
     * 로그아웃 또는 reuse detection 시 호출.
     */
    public void revokeFamily(String familyId, long ttlMs) {
        try {
            redisTemplate.opsForValue().set(
                    FAMILY_REVOKED_PREFIX + familyId,
                    "1",
                    Duration.ofMillis(ttlMs)
            );
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed during family revocation", e);
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * 토큰 가족이 revoke되었는지 확인한다.
     */
    public boolean isFamilyRevoked(String familyId) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(FAMILY_REVOKED_PREFIX + familyId));
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed during family revocation check", e);
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * consume() 결과를 나타내는 열거형.
     */
    public enum ConsumeOutcome {
        OK,
        NOT_FOUND,
        REUSE_DETECTED,
        FAMILY_REVOKED
    }
}