package com.hsp.fitu.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomMessageResponseDTO {
    private List<ChatMessage> messages;
}
