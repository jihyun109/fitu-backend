package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatRoom;
import com.hsp.fitu.dto.ChatRoomCreateRequestDTO;
import com.hsp.fitu.dto.ChatRoomCreateResponseDTO;
import com.hsp.fitu.dto.ChatRoomListResponseDTO;
import com.hsp.fitu.entity.ChatRoomEntity;
import com.hsp.fitu.entity.ChatRoomMemberEntity;
import com.hsp.fitu.repository.ChatRoomMemberRepository;
import com.hsp.fitu.repository.ChatRoomRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

    @Override
    public ChatRoomCreateResponseDTO createChatRoom(Long userId, ChatRoomCreateRequestDTO chatRoomCreateRequestDTO) {
        // 첫 번째 초대 멤버의 프로필 이미지를 채팅방 썸네일로 사용
        Long memberProfileImgId = userRepository.findProfileImgIdById(chatRoomCreateRequestDTO.getMemberIds().get(0));

        // 채팅방 생성 및 저장
        Long chatRoomId = chatRoomRepository.save(ChatRoomEntity.builder()
                        .roomName(chatRoomCreateRequestDTO.getName())
                        .thumbnailImgId(memberProfileImgId)
                        .build())
                .getId();

        // 초대된 멤버들을 채팅방 멤버로 등록
        for (Long memberId : chatRoomCreateRequestDTO.getMemberIds()) {
            chatRoomMemberRepository.save(ChatRoomMemberEntity.builder()
                    .chatRoomId(chatRoomId)
                    .userId(memberId)
                    .build());
        }
        // 채팅방 생성자(요청자) 본인도 멤버로 등록
        chatRoomMemberRepository.save(ChatRoomMemberEntity.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .build());

        return ChatRoomCreateResponseDTO.builder()
                .id(chatRoomId)
                .build();
    }

    @Override
    public ChatRoomListResponseDTO getChatRoomList(Long userId) {
        List<ChatRoom> chatRoomListResponseDTO = chatRoomRepository.getChatRoomList(userId);

        return ChatRoomListResponseDTO.builder()
                .chatRoomList(chatRoomListResponseDTO).build();
    }
}
