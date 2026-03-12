package com.hsp.fitu.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 브로커를 통해 전달되는 채팅 메시지 payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatBrokerMessage {

    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime sendTime;

    /** 부하테스트 레이턴시 측정용 패스스루 필드 (null이면 무시) */
    private Long vuId;
    private Integer seq;
}
