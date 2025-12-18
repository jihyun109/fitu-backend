package com.hsp.fitu.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessage {
    private String senderName;
    private String message;
    private String senderProfileUrl;
    private LocalDateTime sendTime;
}
