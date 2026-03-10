package com.hsp.fitu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ChatMessageRequestDTO {
    private long roomId;
    private String message;

    @JsonProperty("_vuId")
    private Long vuId;

    @JsonProperty("_seq")
    private Integer seq;
}
