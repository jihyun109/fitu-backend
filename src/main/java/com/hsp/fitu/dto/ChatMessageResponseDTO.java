package com.hsp.fitu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChatMessageResponseDTO {
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime sendTime;

    @JsonProperty("_vuId")
    private Long vuId;

    @JsonProperty("_seq")
    private Integer seq;
}
