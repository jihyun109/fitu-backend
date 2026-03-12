package com.hsp.fitu.messaging.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsp.fitu.dto.ChatMessageResponseDTO;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Redis Pub/Sub 구독자.
 * Redis에서 메시지를 수신하여 WebSocket 클라이언트에 브로드캐스트한다.
 *
 * 모든 앱 인스턴스가 동일 채널을 구독하므로,
 * 어느 인스턴스에 연결된 클라이언트든 메시지를 수신할 수 있다.
 *
 * [팬아웃 최적화]
 * - onMessage(): 역직렬화만 수행 후 broadcastExecutor에 위임 → redis-listener 스레드 즉시 해방
 * - broadcast(): DTO를 1회만 직렬화한 byte[]를 모든 convertAndSend에 재사용
 */
@Slf4j
@Component
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;
    private final Executor broadcastExecutor;

    public RedisMessageSubscriber(
            SimpMessageSendingOperations messagingTemplate,
            ObjectMapper objectMapper,
            @Qualifier("broadcastExecutor") Executor broadcastExecutor) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        this.broadcastExecutor = broadcastExecutor;
    }

    /** redis-listener 스레드에서 실행. 역직렬화 후 broadcastExecutor에 위임하고 즉시 반환한다. */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            ChatBrokerMessage brokerMessage = objectMapper.readValue(payload, ChatBrokerMessage.class);
            broadcastExecutor.execute(() -> broadcast(brokerMessage));
        } catch (JsonProcessingException e) {
            log.error("Redis 채팅 메시지 역직렬화 실패: payload={}", payload, e);
        }
    }

    /** broadcastExecutor 스레드에서 실행. DTO를 1회만 직렬화한 byte[]를 모든 convertAndSend에 재사용한다. */
    private void broadcast(ChatBrokerMessage brokerMessage) {
        ChatMessageResponseDTO responseDTO = ChatMessageResponseDTO.builder()
                .roomId(brokerMessage.getRoomId())
                .senderId(brokerMessage.getSenderId())
                .senderName(brokerMessage.getSenderName())
                .message(brokerMessage.getContent())
                .sendTime(brokerMessage.getSendTime())
                .vuId(brokerMessage.getVuId())
                .seq(brokerMessage.getSeq())
                .build();

        try {
            // 직렬화 1회 → 이후 모든 convertAndSend에 byte[] 재사용 (기존: N+1회 반복 직렬화)
            byte[] jsonBytes = objectMapper.writeValueAsBytes(responseDTO);
            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
            MessageHeaders headers = new MessageHeaders(headerMap);

            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + brokerMessage.getRoomId(),
                    jsonBytes,
                    headers
            );
        } catch (JsonProcessingException e) {
            log.error("채팅 브로드캐스트 직렬화 실패: roomId={}", brokerMessage.getRoomId(), e);
        }
    }
}