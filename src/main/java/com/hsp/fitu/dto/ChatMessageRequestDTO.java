package com.hsp.fitu.dto;

import lombok.Getter;

@Getter
public class ChatMessageRequestDTO {
    private long roomId;
    private String message;
}
