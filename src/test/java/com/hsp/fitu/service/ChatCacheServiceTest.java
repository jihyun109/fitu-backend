package com.hsp.fitu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsp.fitu.repository.ChatRoomMemberRepository;
import com.hsp.fitu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatCacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomMemberRepository chatRoomMemberRepository;

    private ChatCacheService chatCacheService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // RedisTemplate.opsForValue()가 호출되면 mock ValueOperations를 반환
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        objectMapper = new ObjectMapper();
        chatCacheService = new ChatCacheService(redisTemplate, userRepository, chatRoomMemberRepository, objectMapper);
    }

    // ===== getSenderName =====

    @Test
    @DisplayName("getSenderName 캐시 히트: Redis에 값이 있으면 DB를 조회하지 않는다")
    void getSenderName_cacheHit_noDbCall() {
        when(valueOperations.get("user:name:100")).thenReturn("캐시된이름");

        String result = chatCacheService.getSenderName(100L);

        assertThat(result).isEqualTo("캐시된이름");
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("getSenderName 캐시 미스: DB에서 조회 후 Redis에 24시간 TTL로 저장한다")
    void getSenderName_cacheMiss_fetchesFromDbAndCaches() {
        when(valueOperations.get("user:name:100")).thenReturn(null);
        when(userRepository.findNameById(100L)).thenReturn("DB이름");

        String result = chatCacheService.getSenderName(100L);

        assertThat(result).isEqualTo("DB이름");
        verify(userRepository).findNameById(100L);
        verify(valueOperations).set("user:name:100", "DB이름", 24, TimeUnit.HOURS);
    }

    // ===== getRoomMemberIds =====

    @Test
    @DisplayName("getRoomMemberIds 캐시 히트: Redis JSON을 역직렬화하여 반환한다")
    void getRoomMemberIds_cacheHit_returnsDeserializedList() {
        when(valueOperations.get("chat:room:members:1")).thenReturn("[100,200,300]");

        List<Long> result = chatCacheService.getRoomMemberIds(1L);

        assertThat(result).containsExactly(100L, 200L, 300L);
        verifyNoInteractions(chatRoomMemberRepository);
    }

    @Test
    @DisplayName("getRoomMemberIds 캐시 미스: DB에서 조회 후 Redis에 JSON으로 저장한다")
    void getRoomMemberIds_cacheMiss_fetchesFromDb() {
        when(valueOperations.get("chat:room:members:1")).thenReturn(null);
        when(chatRoomMemberRepository.findAllUserIdsByChatRoomId(1L)).thenReturn(List.of(100L, 200L));

        List<Long> result = chatCacheService.getRoomMemberIds(1L);

        assertThat(result).containsExactly(100L, 200L);
        verify(chatRoomMemberRepository).findAllUserIdsByChatRoomId(1L);
        verify(valueOperations).set(eq("chat:room:members:1"), anyString(), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    @DisplayName("getRoomMemberIds 역직렬화 실패: DB 폴백으로 정상 반환한다")
    void getRoomMemberIds_deserializationFails_fallsBackToDb() {
        // 잘못된 JSON이 캐시에 저장되어 있는 경우
        when(valueOperations.get("chat:room:members:1")).thenReturn("잘못된JSON");
        when(chatRoomMemberRepository.findAllUserIdsByChatRoomId(1L)).thenReturn(List.of(100L));

        List<Long> result = chatCacheService.getRoomMemberIds(1L);

        assertThat(result).containsExactly(100L);
        verify(chatRoomMemberRepository).findAllUserIdsByChatRoomId(1L);
    }

    // ===== evictRoomMembers =====

    @Test
    @DisplayName("evictRoomMembers: Redis에서 해당 방의 캐시 키를 삭제한다")
    void evictRoomMembers_deletesKey() {
        chatCacheService.evictRoomMembers(1L);

        verify(redisTemplate).delete("chat:room:members:1");
    }
}
