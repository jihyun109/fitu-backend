package com.hsp.fitu.controller;

import com.hsp.fitu.dto.ChatMessageRequestDTO;
import com.hsp.fitu.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * STOMP 메시지 컨트롤러.
 * 클라이언트가 /pub/chat/message 로 SEND한 메시지를 수신한다.
 */
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChatMessageService chatMessageService;

    /**
     * 채팅 메시지 수신 핸들러.
     *
     * 클라이언트 STOMP SEND 목적지: /pub/chat/message
     * Body: { "roomId": 1, "message": "내용" }
     *
     * userId는 CONNECT 시점에 WebSocketAuthChannelInterceptor가
     * 세션 속성(simpSessionAttributes)에 저장해 둔 값을 꺼내 사용한다.
     * 반환값이 없으므로 @SendTo 없이 브로커(Redis)를 통해 직접 브로드캐스트한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageRequestDTO message, @Header("simpSessionAttributes") Map<String, Object> sessionAttrs) {
        Long userId = (Long) sessionAttrs.get("userId");

        chatMessageService.sendMessage(message, userId);
    }
}
