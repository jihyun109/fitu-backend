package com.hsp.fitu.controller;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageRequestDTO message, @Header("simpSessionAttributes") Map<String, Object> sessionAttrs) {
        Long userId = (Long) sessionAttrs.get("userId");

        chatMessageService.sendMessage(message, userId);
    }
}
