package com.hsp.fitu.service;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatMessageResponseDTO;
import com.hsp.fitu.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessageResponseDTO save(ChatMessageRequestDTO message, long userId) {

        return null;
    }
}
