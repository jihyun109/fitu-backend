package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatRoomMessageResponseDTO;

public interface ChatMessageService {

    void sendMessage(ChatMessageRequestDTO message, long userId);

    ChatRoomMessageResponseDTO getChatRoomMessage(Long chatRoomId);
}
