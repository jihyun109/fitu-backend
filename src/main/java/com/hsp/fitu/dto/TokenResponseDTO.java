package com.hsp.fitu.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponseDTO {
    private String token;
    private Long userId;
}
