package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatMessageResponseDTO;
import com.hsp.fitu.entity.ChatMessageEntity;
import com.hsp.fitu.repository.ChatMessageRepository;
import com.hsp.fitu.repository.ChatRoomMemberRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void sendMessage(ChatMessageRequestDTO message, long userId) {
        // 1. 인증된 유저인지 확인 (Principal)

        // 2. 메시지 저장
        ChatMessageResponseDTO responseDTO = save(message, userId);

        // 3. 같은 방에 브로드캐스트
        Long roomId = responseDTO.getRoomId();
        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + roomId,
                responseDTO
        );

        // 4. 해당 방의 멤버들에게 브로드 캐스트
        // 방의 멤버 리스트 get
        List<Long> roomMemberIds = chatRoomMemberRepository.findAllUserIdsByChatRoomId(roomId);
        for (Long memberId : roomMemberIds) {
            messagingTemplate.convertAndSend(
                    "/sub/chat/room/list/" + memberId,
                    responseDTO
            );
        }
    }

    private ChatMessageResponseDTO save(ChatMessageRequestDTO message, long userId) {
        // 메시지 db에 저장
        ChatMessageEntity chatMessageEntity = chatMessageRepository.save(ChatMessageEntity.builder()
                .chatRoomId(message.getRoomId())
                .content(message.getMessage())
                .messageType(ChatMessageEntity.ChatMessageType.TALK)
                .senderId(userId)
                .build());

        // 전송자 이름 get
        String senderName = userRepository.findNameById(userId);

        return ChatMessageResponseDTO.builder()
                .message(chatMessageEntity.getContent())
                .roomId(chatMessageEntity.getChatRoomId())
                .senderId(userId)
                .senderName(senderName)
                .build();
    }
}
