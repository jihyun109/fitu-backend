package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessage;
import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatRoomMessageResponseDTO;
import com.hsp.fitu.entity.ChatMessageEntity;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import com.hsp.fitu.messaging.ChatMessagePersistBuffer;
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
    private final ChatMessagePersistBuffer chatMessagePersistBuffer;

    // Micrometer 메트릭: Prometheus에서 수집되어 Grafana 대시보드에 표시된다
    private final Counter messagesSentCounter;   // 전송 처리량 (TPS 계산용)
    private final Timer messageSendTimer;        // 전송 처리 시간 (p50/p95/p99 분포)

    public ChatMessageServiceImpl(
            ChatMessageRepository chatMessageRepository,
            ChatCacheService chatCacheService,
            MessageBrokerPort messageBrokerPort,
            ChatMessagePersistBuffer chatMessagePersistBuffer,
            MeterRegistry meterRegistry) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatCacheService = chatCacheService;
        this.messageBrokerPort = messageBrokerPort;
        this.chatMessagePersistBuffer = chatMessagePersistBuffer;

        this.messagesSentCounter = meterRegistry.counter("chat.messages.sent");
        this.messageSendTimer = meterRegistry.timer("chat.message.send.duration");
    }

    @Override
    public void sendMessage(ChatMessageRequestDTO message, long userId) {
        messageSendTimer.record(() -> doSendMessage(message, userId));
    }

    private void doSendMessage(ChatMessageRequestDTO message, long userId) {
        LocalDateTime sendTime = LocalDateTime.now();

        // 1. 발신자 이름 — Redis 캐시에서 조회 (TTL 24h, 캐시 미스 시 DB 폴백)
        String senderName = chatCacheService.getSenderName(userId);

        // 2. Redis Pub/Sub으로 실시간 메시지 전달
        //    해당 채팅방을 구독 중인 모든 WebSocket 클라이언트가 수신
        try {
            messageBrokerPort.publish(ChatBrokerMessage.builder()
                    .roomId(message.getRoomId())
                    .senderId(userId)
                    .senderName(senderName)
                    .content(message.getMessage())
                    .sendTime(sendTime)
                    ._vuId(message.get_vuId())
                    ._seq(message.get_seq())
                    .build());
        } catch (Exception e) {
            log.warn("Redis 메시지 발행 실패. roomId={}, senderId={}", message.getRoomId(), userId, e);
        }

        // 3. DB 저장을 Redis Stream에 위임 (비동기)
        //    ChatMessagePersistConsumer가 Stream에서 꺼내서 배치 INSERT
        //    메시지 전송 경로에서 DB 의존성을 제거
        try {
            chatMessagePersistBuffer.enqueue(message.getRoomId(), userId, message.getMessage(), sendTime);
        } catch (Exception e) {
            log.warn("메시지 영구 저장 큐 추가 실패 — 실시간 전달은 완료됨. roomId={}, senderId={}",
                    message.getRoomId(), userId, e);
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
