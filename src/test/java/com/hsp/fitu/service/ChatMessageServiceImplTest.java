package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.entity.ChatMessageEntity;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import com.hsp.fitu.messaging.MessageBrokerPort;
import com.hsp.fitu.repository.ChatMessageRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Mockito 단위 테스트의 핵심 개념:
 *
 * 1. @Mock: 가짜(mock) 객체를 만든다. 실제 DB나 Redis에 접근하지 않는다.
 *    - when(mock.method()).thenReturn(값) → mock 메서드 호출 시 지정한 값을 반환하도록 설정
 *    - verify(mock).method() → 해당 메서드가 실제로 호출되었는지 검증
 *
 * 2. 테스트 패턴 (Given-When-Then):
 *    - given: 테스트 조건 설정 (mock 반환값 지정)
 *    - when: 테스트 대상 메서드 호출
 *    - then: 결과 검증 (반환값 확인, 메서드 호출 여부 확인)
 */
@ExtendWith(MockitoExtension.class) // JUnit5에서 Mockito를 사용하기 위한 설정
class ChatMessageServiceImplTest {

    // @Mock: 실제 구현체 대신 가짜 객체를 생성한다. DB에 접근하지 않는다.
    @Mock
    private ChatMessageRepository chatMessageRepository;

    // @Mock: 실제 Redis에 접근하지 않는 가짜 캐시 서비스
    @Mock
    private ChatCacheService chatCacheService;

    // @Mock: 실제 Redis Pub/Sub에 발행하지 않는 가짜 브로커
    @Mock
    private MessageBrokerPort messageBrokerPort;

    @Mock
    private com.hsp.fitu.messaging.ChatMessagePersistBuffer chatMessagePersistBuffer;

    // 테스트 대상 클래스. 위 Mock들 + SimpleMeterRegistry를 주입받는다.
    private ChatMessageServiceImpl chatMessageService;

    // @BeforeEach: 각 테스트 메서드가 실행되기 전에 매번 호출된다.
    // MeterRegistry는 Mock이 아닌 실제 구현체(SimpleMeterRegistry)를 사용한다.
    // SimpleMeterRegistry: 테스트용 경량 MeterRegistry. Prometheus 없이 메트릭 동작을 검증할 수 있다.
    @BeforeEach
    void setUp() {
        chatMessageService = new ChatMessageServiceImpl(
                chatMessageRepository,
                chatCacheService,
                messageBrokerPort,
                chatMessagePersistBuffer,
                new SimpleMeterRegistry()
        );
    }

    /**
     * 테스트용 ChatMessageRequestDTO를 생성하는 헬퍼 메서드.
     * ChatMessageRequestDTO에 setter가 없고 @Getter만 있으므로
     * 리플렉션(Java의 private 필드에 강제 접근하는 기법)으로 값을 설정한다.
     */
    private ChatMessageRequestDTO createRequest(long roomId, String message) {
        try {
            ChatMessageRequestDTO dto = new ChatMessageRequestDTO();

            // 리플렉션: private 필드 "roomId"를 가져와서 접근 가능하게 만든 뒤 값 설정
            var roomIdField = ChatMessageRequestDTO.class.getDeclaredField("roomId");
            roomIdField.setAccessible(true);  // private 접근 제한 해제
            roomIdField.setLong(dto, roomId); // 값 설정

            var messageField = ChatMessageRequestDTO.class.getDeclaredField("message");
            messageField.setAccessible(true);
            messageField.set(dto, message);

            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * DB에서 save() 후 반환될 엔티티를 미리 만들어두는 헬퍼.
     * 실제 DB에 저장하는 게 아니라, mock이 이 객체를 반환하도록 설정하는 데 사용한다.
     */
    private ChatMessageEntity createSavedEntity(long roomId, long senderId, String content) {
        return ChatMessageEntity.builder()
                .id(1L)
                .chatRoomId(roomId)
                .senderId(senderId)
                .messageType(ChatMessageEntity.ChatMessageType.TALK)
                .content(content)
                .createdAt(LocalDateTime.of(2026, 3, 25, 10, 0))
                .build();
    }

    @Test
    @DisplayName("정상 메시지 전송: 캐시 조회 → 브로커 발행 → Redis Stream 저장이 수행된다")
    void sendMessage_success_publishesAndEnqueues() {
        // === given ===
        long roomId = 1L;
        long userId = 100L;
        ChatMessageRequestDTO request = createRequest(roomId, "안녕하세요");

        when(chatCacheService.getSenderName(userId)).thenReturn("홍길동");

        // === when ===
        chatMessageService.sendMessage(request, userId);

        // === then ===
        // 1. Redis Pub/Sub 발행 검증
        ArgumentCaptor<ChatBrokerMessage> captor = ArgumentCaptor.forClass(ChatBrokerMessage.class);
        verify(messageBrokerPort).publish(captor.capture());

        ChatBrokerMessage published = captor.getValue();
        assertThat(published.getRoomId()).isEqualTo(roomId);
        assertThat(published.getSenderId()).isEqualTo(userId);
        assertThat(published.getSenderName()).isEqualTo("홍길동");
        assertThat(published.getContent()).isEqualTo("안녕하세요");

        // 2. Redis Stream에 DB 저장 요청이 들어갔는지 검증
        verify(chatMessagePersistBuffer).enqueue(eq(roomId), eq(userId), eq("안녕하세요"), any(LocalDateTime.class));

        // 3. DB에 직접 save하지 않았는지 검증 (DB 저장은 Consumer가 비동기로 처리)
        verifyNoInteractions(chatMessageRepository);
    }

    @Test
    @DisplayName("캐시 히트: chatCacheService가 값을 반환하면 추가 DB 조회 없이 완료된다")
    void sendMessage_cacheHit_noAdditionalDbCall() {
        // === given ===
        ChatMessageRequestDTO request = createRequest(1L, "테스트");

        when(chatCacheService.getSenderName(100L)).thenReturn("캐시된이름");

        // === when ===
        chatMessageService.sendMessage(request, 100L);

        // === then ===
        verify(chatCacheService).getSenderName(100L);
        // DB 직접 접근 없음 (Consumer가 비동기로 처리)
        verifyNoInteractions(chatMessageRepository);
    }

    @Test
    @DisplayName("Redis publish 실패 시 예외 없이 정상 반환되고, Stream 저장은 계속 진행된다")
    void sendMessage_publishFails_enqueueStillCalled() {
        // === given ===
        ChatMessageRequestDTO request = createRequest(1L, "메시지");

        when(chatCacheService.getSenderName(100L)).thenReturn("이름");
        doThrow(new BusinessException(ErrorCode.CHAT_MESSAGE_PUBLISH_FAILED))
                .when(messageBrokerPort).publish(any());

        // === when ===
        chatMessageService.sendMessage(request, 100L);

        // === then ===
        // publish가 실패해도 예외가 전파되지 않고, Stream 저장은 계속 진행됨
        verify(messageBrokerPort).publish(any());
        verify(chatMessagePersistBuffer).enqueue(eq(1L), eq(100L), eq("메시지"), any(LocalDateTime.class));
    }
}
