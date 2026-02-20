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
        // 1. 메시지 DB 저장
        ChatMessageEntity saved = chatMessageRepository.save(ChatMessageEntity.builder()
                .chatRoomId(message.getRoomId())
                .content(message.getMessage())
                .messageType(ChatMessageEntity.ChatMessageType.TALK)
                .senderId(userId)
                .build());

        // 2. 전송자 이름 및 멤버 목록 조회
        String senderName = userRepository.findNameById(userId);
        List<Long> roomMemberIds = chatRoomMemberRepository.findAllUserIdsByChatRoomId(saved.getChatRoomId());

        // 3. 브로커를 통해 브로드캐스트 (Redis → 전체 인스턴스의 WebSocket 구독자에게 전달)
        messageBrokerPort.publish(ChatBrokerMessage.builder()
                .roomId(saved.getChatRoomId())
                .senderId(userId)
                .senderName(senderName)
                .content(saved.getContent())
                .roomMemberIds(roomMemberIds)
                .build());
    }

    @Override
    public ChatRoomMessageResponseDTO getChatRoomMessage(Long chatRoomId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findChatMessagesByChatRoomId(chatRoomId);
        return ChatRoomMessageResponseDTO.builder()
                .messages(chatMessageList).build();
    }
}
