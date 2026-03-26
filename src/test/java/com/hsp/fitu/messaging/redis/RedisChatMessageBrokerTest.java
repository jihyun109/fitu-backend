package com.hsp.fitu.messaging.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisChatMessageBrokerTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private RedisChatMessageBroker broker;
    private final ChannelTopic topic = new ChannelTopic("chat:messages");

    @BeforeEach
    void setUp() {
        broker = new RedisChatMessageBroker(redisTemplate, objectMapper, topic, new SimpleMeterRegistry());
    }

    @Test
    @DisplayName("정상 발행: redisTemplate.convertAndSend()가 chat:messages 토픽으로 호출된다")
    void publish_success_sendsToTopic() throws Exception {
        // given
        ChatBrokerMessage message = ChatBrokerMessage.builder()
                .roomId(1L).senderId(100L).senderName("이름")
                .content("메시지").sendTime(LocalDateTime.now())
                .roomMemberIds(List.of(100L)).build();

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"json\":\"payload\"}");

        // when
        broker.publish(message);

        // then
        verify(redisTemplate).convertAndSend(eq("chat:messages"), eq("{\"json\":\"payload\"}"));
    }

    @Test
    @DisplayName("직렬화 실패 시 BusinessException(CHAT_MESSAGE_PUBLISH_FAILED)을 던진다")
    void publish_serializationFails_throwsException() throws Exception {
        // given
        ChatBrokerMessage message = ChatBrokerMessage.builder()
                .roomId(1L).senderId(100L).build();

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("직렬화 실패") {});

        // when & then
        assertThatThrownBy(() -> broker.publish(message))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assert be.getErrorCode() == ErrorCode.CHAT_MESSAGE_PUBLISH_FAILED;
                });
    }
}
