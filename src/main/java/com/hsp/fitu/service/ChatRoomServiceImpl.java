package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatRoomCreateRequestDTO;
import com.hsp.fitu.dto.ChatRoomCreateResponseDTO;
import com.hsp.fitu.entity.ChatRoomEntity;
import com.hsp.fitu.entity.ChatRoomMemberEntity;
import com.hsp.fitu.repository.ChatRoomMemberRepository;
import com.hsp.fitu.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Override
    public ChatRoomCreateResponseDTO createChatRoom(Long userId, ChatRoomCreateRequestDTO chatRoomCreateRequestDTO) {
        // 채팅방 db에 저장
        Long chatRoomId = chatRoomRepository.save(ChatRoomEntity.builder()
                        .roomName(chatRoomCreateRequestDTO.getName())
                        .build())
                .getId();

        // 채팅방 멤버 db에 저장
        for (Long memberId : chatRoomCreateRequestDTO.getMemberIds()) {
            chatRoomMemberRepository.save(ChatRoomMemberEntity.builder()
                    .chatRoomId(chatRoomId)
                    .userId(memberId)
                    .build());
        }

        return ChatRoomCreateResponseDTO.builder()
                .id(chatRoomId)
                .build();
    }
}
