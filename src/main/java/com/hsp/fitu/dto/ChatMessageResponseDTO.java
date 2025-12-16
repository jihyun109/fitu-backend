package com.hsp.fitu.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatMessageResponseDTO {
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String message;
}
