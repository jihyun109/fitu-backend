package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessage;
import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatRoomMessageResponseDTO;
import com.hsp.fitu.entity.ChatMessageEntity;
import com.hsp.fitu.messaging.ChatBrokerMessage;
import com.hsp.fitu.messaging.MessageBrokerPort;
import com.hsp.fitu.repository.ChatMessageRepository;
import com.hsp.fitu.repository.ChatRoomMemberRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MessageBrokerPort messageBrokerPort;

    @Override
    public void sendMessage(ChatMessageRequestDTO message, long userId) {
        // 1. 메시지를 DB에 영구 저장 (메시지 이력 보존 및 조회에 사용)
        ChatMessageEntity saved = chatMessageRepository.save(ChatMessageEntity.builder()
                .chatRoomId(message.getRoomId())
                .content(message.getMessage())
                .messageType(ChatMessageEntity.ChatMessageType.TALK)
                .senderId(userId)
                .build());

        // 2. 클라이언트에 노출할 발신자 이름과, Redis 구독자가 라우팅에 사용할 멤버 목록 조회
        //    roomMemberIds를 payload에 포함시켜 구독자가 DB 재조회 없이 브로드캐스트 가능하게 한다
        String senderName = userRepository.findNameById(userId);
        List<Long> roomMemberIds = chatRoomMemberRepository.findAllUserIdsByChatRoomId(saved.getChatRoomId());

        // 3. Redis Pub/Sub을 통해 전체 서버 인스턴스에 메시지 발행
        //    어느 인스턴스에 WebSocket이 연결된 클라이언트든 수신 가능하게 한다
        messageBrokerPort.publish(ChatBrokerMessage.builder()
                .roomId(saved.getChatRoomId())
                .senderId(userId)
                .senderName(senderName)
                .content(saved.getContent())
                .roomMemberIds(roomMemberIds)
                .build());
    }

    /**
     * 채팅방 입장 시 과거 메시지 이력을 반환한다.
     * WebSocket 실시간 메시지와 달리, 이 메서드는 REST API로 호출된다.
     */
    @Override
    public ChatRoomMessageResponseDTO getChatRoomMessage(Long chatRoomId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findChatMessagesByChatRoomId(chatRoomId);
        return ChatRoomMessageResponseDTO.builder()
                .messages(chatMessageList).build();
    }
}
