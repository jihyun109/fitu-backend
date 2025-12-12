package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatMessageResponseDTO;

public interface ChatMessageService {
    ChatMessageResponseDTO save(ChatMessageRequestDTO message, long userId);
}
