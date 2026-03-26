package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatRoomMessageResponseDTO;

import java.time.LocalDateTime;

public interface ChatMessageService {

    void sendMessage(ChatMessageRequestDTO message, long userId);

    ChatRoomMessageResponseDTO getChatRoomMessages(Long chatRoomId, LocalDateTime before, int limit);

    ChatRoomMessageResponseDTO getChatRoomMessageAfter(Long chatRoomId, LocalDateTime after);
}
