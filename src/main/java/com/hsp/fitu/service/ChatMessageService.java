package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessageRequestDTO;

public interface ChatMessageService {

    void sendMessage(ChatMessageRequestDTO message, long userId);
}
