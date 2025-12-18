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
        // 채팅방 thumbnail img get
        Long memberProfileImgId = userRepository.findProfileImgIdById(chatRoomCreateRequestDTO.getMemberIds().get(0));

        // 채팅방 db에 저장
        Long chatRoomId = chatRoomRepository.save(ChatRoomEntity.builder()
                        .roomName(chatRoomCreateRequestDTO.getName())
                        .thumbnailImgId(memberProfileImgId)
                        .build())
                .getId();

        // 채팅방 멤버 db에 저장
        for (Long memberId : chatRoomCreateRequestDTO.getMemberIds()) {
            chatRoomMemberRepository.save(ChatRoomMemberEntity.builder()
                    .chatRoomId(chatRoomId)
                    .userId(memberId)
                    .build());
        }
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

        for (ChatRoom chatRoom : chatRoomListResponseDTO) {
            Long chatRoomId = chatRoom.getRoomId();
            String url = chatRoomRepository.getChatRoomImg(chatRoomId, userId);
            chatRoom.setImgUrl(url);
        }

        return ChatRoomListResponseDTO.builder()
                .chatRoomList(chatRoomRepository.getChatRoomList(userId)).build();
    }
}
