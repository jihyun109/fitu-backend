package com.hsp.fitu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsp.fitu.repository.ChatRoomMemberRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 채팅 메시지 전송 시 반복 조회되는 값을 Redis에 캐싱한다.
 *
 * - user:name:{userId}          TTL 24h — 발신자 이름 (변경 빈도 낮음)
 * - chat:room:members:{roomId}  TTL 24h — 방 멤버 ID 목록 (변경 시 무효화 필요)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCacheService {

    private static final String KEY_USER_NAME    = "user:name:";
    private static final String KEY_ROOM_MEMBERS = "chat:room:members:";
    private static final long   TTL_HOURS        = 24;

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ObjectMapper objectMapper;

    public String getSenderName(Long userId) {
        String key   = KEY_USER_NAME + userId;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        String name = userRepository.findNameById(userId);
        redisTemplate.opsForValue().set(key, name, TTL_HOURS, TimeUnit.HOURS);
        return name;
    }

    public List<Long> getRoomMemberIds(Long roomId) {
        String key    = KEY_ROOM_MEMBERS + roomId;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<Long>>() {});
            } catch (JsonProcessingException e) {
                log.warn("방 멤버 캐시 역직렬화 실패, DB 재조회: roomId={}", roomId, e);
            }
        }
        List<Long> members = chatRoomMemberRepository.findAllUserIdsByChatRoomId(roomId);
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(members),
                    TTL_HOURS, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.warn("방 멤버 캐시 직렬화 실패: roomId={}", roomId, e);
        }
        return members;
    }

    /** 방 멤버 변경(입장/퇴장) 시 캐시 무효화 */
    public void evictRoomMembers(Long roomId) {
        redisTemplate.delete(KEY_ROOM_MEMBERS + roomId);
    }
}
