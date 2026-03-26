package com.hsp.fitu.messaging.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedisMessageSubscriberTest {

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    private ObjectMapper objectMapper;
    private RedisMessageSubscriber subscriber;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 테스트에서는 broadcastExecutor를 동기 실행으로 대체 — Runnable::run은 즉시 실행
        Executor syncExecutor = Runnable::run;

        subscriber = new RedisMessageSubscriber(
                messagingTemplate,
                objectMapper,
                syncExecutor,
                new SimpleMeterRegistry()
        );
    }

    @Test
    @DisplayName("정상 메시지 수신 시 /sub/chat/room/{roomId}로 브로드캐스트한다")
    void onMessage_validPayload_broadcastsToRoom() throws Exception {
        // given
        ChatBrokerMessage brokerMessage = ChatBrokerMessage.builder()
                .roomId(1L)
                .senderId(100L)
                .senderName("홍길동")
                .content("안녕하세요")
                .sendTime(LocalDateTime.of(2026, 3, 25, 10, 0))
                .roomMemberIds(List.of(100L, 200L))
                .build();

        String payload = objectMapper.writeValueAsString(brokerMessage);
        DefaultMessage redisMessage = new DefaultMessage(
                "chat:messages".getBytes(StandardCharsets.UTF_8),
                payload.getBytes(StandardCharsets.UTF_8)
        );

        // when
        subscriber.onMessage(redisMessage, null);

        // then — /sub/chat/room/1 로 send가 호출되었는지 검증
        verify(messagingTemplate).send(eq("/sub/chat/room/1"), any());
    }

    @Test
    @DisplayName("잘못된 JSON 수신 시 예외 없이 로그만 남기고 종료한다")
    void onMessage_invalidJson_doesNotThrow() {
        // given
        DefaultMessage redisMessage = new DefaultMessage(
                "chat:messages".getBytes(StandardCharsets.UTF_8),
                "invalid json!!!".getBytes(StandardCharsets.UTF_8)
        );

        // when — 예외가 발생하지 않아야 한다
        subscriber.onMessage(redisMessage, null);

        // then — broadcast가 호출되지 않음
        verify(messagingTemplate, never()).send(any(String.class), any());
    }
}
