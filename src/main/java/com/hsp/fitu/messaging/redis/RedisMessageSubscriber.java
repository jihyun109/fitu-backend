package com.hsp.fitu.messaging.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsp.fitu.dto.ChatMessageResponseDTO;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Redis Pub/Sub 구독자.
 * Redis에서 메시지를 수신하여 WebSocket 클라이언트에 브로드캐스트한다.
 *
 * 모든 서버 인스턴스가 Redis를 구독하므로,
 * 어느 인스턴스로 WebSocket 연결된 클라이언트든 메시지를 수신할 수 있다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            ChatBrokerMessage brokerMessage = objectMapper.readValue(payload, ChatBrokerMessage.class);
            broadcast(brokerMessage);
        } catch (JsonProcessingException e) {
            log.error("Redis 채팅 메시지 역직렬화 실패: payload={}", payload, e);
        }
    }

    private void broadcast(ChatBrokerMessage brokerMessage) {
        ChatMessageResponseDTO responseDTO = ChatMessageResponseDTO.builder()
                .roomId(brokerMessage.getRoomId())
                .senderId(brokerMessage.getSenderId())
                .senderName(brokerMessage.getSenderName())
                .message(brokerMessage.getContent())
                .build();

        // 채팅방 구독자에게 메시지 전달
        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + brokerMessage.getRoomId(),
                responseDTO
        );

        // 각 멤버의 채팅 목록 업데이트
        for (Long memberId : brokerMessage.getRoomMemberIds()) {
            messagingTemplate.convertAndSend(
                    "/sub/chat/room/list/" + memberId,
                    responseDTO
            );
        }
    }
}
