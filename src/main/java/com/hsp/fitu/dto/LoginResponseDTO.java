package com.hsp.fitu.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDTO {
    private String token;
}
