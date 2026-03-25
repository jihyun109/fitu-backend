package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.entity.ChatMessageEntity;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import com.hsp.fitu.messaging.MessageBrokerPort;
import com.hsp.fitu.repository.ChatMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Mockito 단위 테스트의 핵심 개념:
 *
 * 1. @Mock: 가짜(mock) 객체를 만든다. 실제 DB나 Redis에 접근하지 않는다.
 *    - when(mock.method()).thenReturn(값) → mock 메서드 호출 시 지정한 값을 반환하도록 설정
 *    - verify(mock).method() → 해당 메서드가 실제로 호출되었는지 검증
 *
 * 2. @InjectMocks: 위에서 만든 @Mock 객체들을 자동으로 주입하여 테스트 대상 객체를 생성한다.
 *    → 즉, ChatMessageServiceImpl의 생성자에 mock들이 들어간다.
 *
 * 3. 테스트 패턴 (Given-When-Then):
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

    // @InjectMocks: 위 3개의 Mock을 주입받아 실제 ChatMessageServiceImpl 인스턴스를 생성한다.
    // 테스트 대상 클래스만 진짜이고, 의존하는 객체들은 모두 가짜이다.
    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

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
    @DisplayName("정상 메시지 전송: DB 저장 → 캐시 조회 → 브로커 발행이 순서대로 수행된다")
    void sendMessage_success_savesAndPublishes() {
        // === given: 테스트 조건 설정 ===
        long roomId = 1L;
        long userId = 100L;
        ChatMessageRequestDTO request = createRequest(roomId, "안녕하세요");
        ChatMessageEntity saved = createSavedEntity(roomId, userId, "안녕하세요");

        // when().thenReturn(): "이 메서드가 호출되면 이 값을 반환해라"라는 행동 규칙 설정
        // any(ChatMessageEntity.class) → 어떤 ChatMessageEntity가 들어와도 saved를 반환
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(saved);
        // getSenderName(100L)이 호출되면 "홍길동"을 반환
        when(chatCacheService.getSenderName(userId)).thenReturn("홍길동");
        // getRoomMemberIds(1L)이 호출되면 [100, 200] 반환
        when(chatCacheService.getRoomMemberIds(roomId)).thenReturn(List.of(100L, 200L));

        // === when: 테스트 대상 메서드 실행 ===
        chatMessageService.sendMessage(request, userId);

        // === then: 결과 검증 ===

        // verify(): "이 mock의 이 메서드가 실제로 호출되었는가?" 검증
        // → chatMessageRepository.save()가 1번 호출되었는지 확인
        verify(chatMessageRepository).save(any(ChatMessageEntity.class));

        // ArgumentCaptor: publish()에 전달된 인자를 "캡처"해서 내용을 검증할 수 있다.
        // → messageBrokerPort.publish(???)에서 ???에 뭐가 들어갔는지 잡아낸다.
        ArgumentCaptor<ChatBrokerMessage> captor = ArgumentCaptor.forClass(ChatBrokerMessage.class);
        verify(messageBrokerPort).publish(captor.capture()); // publish 호출을 검증하면서 인자를 캡처

        // 캡처한 ChatBrokerMessage의 필드값들이 올바른지 하나씩 검증
        ChatBrokerMessage published = captor.getValue();
        assertThat(published.getRoomId()).isEqualTo(roomId);
        assertThat(published.getSenderId()).isEqualTo(userId);
        assertThat(published.getSenderName()).isEqualTo("홍길동");
        assertThat(published.getContent()).isEqualTo("안녕하세요");
        assertThat(published.getRoomMemberIds()).containsExactly(100L, 200L);
        assertThat(published.getSendTime()).isEqualTo(saved.getCreatedAt());
    }

    @Test
    @DisplayName("캐시 히트: chatCacheService가 값을 반환하면 추가 DB 조회 없이 완료된다")
    void sendMessage_cacheHit_noAdditionalDbCall() {
        // === given ===
        ChatMessageRequestDTO request = createRequest(1L, "테스트");
        ChatMessageEntity saved = createSavedEntity(1L, 100L, "테스트");

        when(chatMessageRepository.save(any())).thenReturn(saved);
        when(chatCacheService.getSenderName(100L)).thenReturn("캐시된이름");
        when(chatCacheService.getRoomMemberIds(1L)).thenReturn(List.of(100L, 200L));

        // === when ===
        chatMessageService.sendMessage(request, 100L);

        // === then ===
        // verify(): 각 캐시 메서드가 정확히 1번 호출되었는지 확인
        verify(chatCacheService).getSenderName(100L);
        verify(chatCacheService).getRoomMemberIds(1L);

        // only(): chatMessageRepository에서 save() 외에 다른 메서드는 호출되지 않았는지 확인
        // → 캐시가 동작해서 추가 DB 조회(findNameById 등)가 일어나지 않았음을 보장
        verify(chatMessageRepository, only()).save(any());
    }

    @Test
    @DisplayName("Redis publish 실패 시 BusinessException이 발생하지만, DB 저장은 이미 완료된 상태이다")
    void sendMessage_publishFails_dbSaveAlreadyCommitted() {
        // === given ===
        ChatMessageRequestDTO request = createRequest(1L, "메시지");
        ChatMessageEntity saved = createSavedEntity(1L, 100L, "메시지");

        when(chatMessageRepository.save(any())).thenReturn(saved);
        when(chatCacheService.getSenderName(100L)).thenReturn("이름");
        when(chatCacheService.getRoomMemberIds(1L)).thenReturn(List.of(100L));

        // doThrow(): "이 메서드가 호출되면 예외를 던져라" — publish 실패를 시뮬레이션
        // void 메서드는 when().thenThrow() 대신 doThrow().when() 형태를 사용해야 한다
        doThrow(new BusinessException(ErrorCode.CHAT_MESSAGE_PUBLISH_FAILED))
                .when(messageBrokerPort).publish(any());

        // === when & then ===
        // assertThatThrownBy(): 람다 안의 코드가 예외를 던지는지 검증
        assertThatThrownBy(() -> chatMessageService.sendMessage(request, 100L))
                // 발생한 예외가 BusinessException 타입인지 확인
                .isInstanceOf(BusinessException.class)
                // 예외 객체의 errorCode가 CHAT_MESSAGE_PUBLISH_FAILED인지 확인
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CHAT_MESSAGE_PUBLISH_FAILED));

        // 핵심 검증: publish()가 실패해도 save()는 그 전에 이미 호출됨
        // → 메시지는 DB에 저장된 상태. Redis만 실패한 것이므로 재연결 시 복구 가능
        verify(chatMessageRepository).save(any());
    }
}
