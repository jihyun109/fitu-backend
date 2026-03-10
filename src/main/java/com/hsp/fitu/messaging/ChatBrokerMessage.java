package com.hsp.fitu.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 브로커를 통해 전달되는 채팅 메시지 payload.
 * roomMemberIds를 포함하여 구독자가 DB 조회 없이 라우팅 가능하게 한다.
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

    /** 채팅 목록 업데이트 대상 멤버 ID 목록 */
    private List<Long> roomMemberIds;

    /** 부하테스트 레이턴시 측정용 패스스루 필드 (null이면 무시) */
    private Long vuId;
    private Integer seq;
}
