package com.hsp.fitu.controller;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.dto.ChatMessageResponseDTO;
import com.hsp.fitu.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageRequestDTO message, Principal principal) {

        // 1. 인증된 유저인지 확인 (Principal)
        long userId = Long.parseLong(principal.getName());

        // 2. 메시지 저장
        ChatMessageResponseDTO responseDTO = chatMessageService.save(message, userId);

        // 3. 같은 방에 브로드캐스트
        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + message.getRoomId(),
                responseDTO
        );

        // 4. 해당 방의 멤버들에게 브로드 캐스트
        // 방의 멤버 리스트 get
//        messagingTemplate.convertAndSend(
//                "/sub/chat/room/list" + message.getRoomId(),
//                responseDTO
//        );
    }
}
