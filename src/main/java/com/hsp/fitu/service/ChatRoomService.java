package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatRoomCreateRequestDTO;
import com.hsp.fitu.dto.ChatRoomCreateResponseDTO;

public interface ChatRoomService {
    ChatRoomCreateResponseDTO createChatRoom(Long userId, ChatRoomCreateRequestDTO chatRoomCreateRequestDTO);
}
