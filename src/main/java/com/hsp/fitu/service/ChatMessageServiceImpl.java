package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessage;
import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatRoomMessageResponseDTO;
import com.hsp.fitu.entity.ChatMessageEntity;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import com.hsp.fitu.messaging.MessageBrokerPort;
import com.hsp.fitu.repository.ChatMessageRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatCacheService chatCacheService;
    private final MessageBrokerPort messageBrokerPort;

    // Micrometer 메트릭: Prometheus에서 수집되어 Grafana 대시보드에 표시된다
    private final Counter messagesSentCounter;   // 전송 처리량 (TPS 계산용)
    private final Timer messageSendTimer;        // 전송 처리 시간 (p50/p95/p99 분포)

    public ChatMessageServiceImpl(
            ChatMessageRepository chatMessageRepository,
            ChatCacheService chatCacheService,
            MessageBrokerPort messageBrokerPort,
            MeterRegistry meterRegistry) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatCacheService = chatCacheService;
        this.messageBrokerPort = messageBrokerPort;

        this.messagesSentCounter = meterRegistry.counter("chat.messages.sent");
        this.messageSendTimer = meterRegistry.timer("chat.message.send.duration");
    }

    @Override
    public void sendMessage(ChatMessageRequestDTO message, long userId) {
        messageSendTimer.record(() -> doSendMessage(message, userId));
    }

    private void doSendMessage(ChatMessageRequestDTO message, long userId) {
        // 1. 메시지를 DB에 영구 저장 (메시지 이력 보존 및 조회에 사용)
        ChatMessageEntity saved = chatMessageRepository.save(ChatMessageEntity.builder()
                .chatRoomId(message.getRoomId())
                .content(message.getMessage())
                .messageType(ChatMessageEntity.ChatMessageType.TALK)
                .senderId(userId)
                .build());

        // 2. 발신자 이름, 방 멤버 목록 — Redis 캐시에서 조회 (TTL 24h, 캐시 미스 시 DB 폴백)
        //    메시지 전송마다 발생하던 DB 조회 2건 제거
        String senderName = chatCacheService.getSenderName(userId);
        List<Long> roomMemberIds = chatCacheService.getRoomMemberIds(saved.getChatRoomId());

        // 3. Redis Pub/Sub을 통해 전체 서버 인스턴스에 메시지 발행
        //    어느 인스턴스에 WebSocket이 연결된 클라이언트든 수신 가능하게 한다
        //    Redis 장애 시에도 DB에는 이미 저장되었으므로, 수신자는 재연결 시 REST API로 복구 가능
        try {
            messageBrokerPort.publish(ChatBrokerMessage.builder()
                    .roomId(saved.getChatRoomId())
                    .senderId(userId)
                    .senderName(senderName)
                    .content(saved.getContent())
                    .sendTime(saved.getCreatedAt())
                    .roomMemberIds(roomMemberIds)
                    ._vuId(message.get_vuId())
                    ._seq(message.get_seq())
                    .build());
        } catch (Exception e) {
            log.warn("Redis 메시지 발행 실패 — DB 저장은 완료됨. roomId={}, senderId={}", saved.getChatRoomId(), userId, e);
        }

        messagesSentCounter.increment();
    }

    /**
     * 채팅방 메시지 이력을 커서 기반 페이지네이션으로 반환한다.
     * before가 null이면 최신 메시지부터, 있으면 해당 시각 이전 메시지부터 limit건을 반환한다.
     * DB에서 역순(최신→과거)으로 가져온 뒤, 클라이언트 표시를 위해 시간순으로 뒤집는다.
     */
    @Override
    public ChatRoomMessageResponseDTO getChatRoomMessages(Long chatRoomId, LocalDateTime before, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<ChatMessage> messages;

        if (before != null) {
            messages = chatMessageRepository.findMessagesBefore(chatRoomId, before, pageable);
        } else {
            messages = chatMessageRepository.findRecentMessages(chatRoomId, pageable);
        }

        // DB에서 DESC로 가져왔으므로 시간순(ASC)으로 뒤집어서 반환
        List<ChatMessage> reversed = new java.util.ArrayList<>(messages);
        java.util.Collections.reverse(reversed);

        return ChatRoomMessageResponseDTO.builder()
                .messages(reversed).build();
    }

    /**
     * 재연결 후 누락된 메시지를 보충할 때 사용한다.
     * after 이후에 저장된 메시지만 반환한다.
     */
    @Override
    public ChatRoomMessageResponseDTO getChatRoomMessageAfter(Long chatRoomId, LocalDateTime after) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findChatMessagesByChatRoomIdAfter(chatRoomId, after);
        return ChatRoomMessageResponseDTO.builder()
                .messages(chatMessageList).build();
    }
}
