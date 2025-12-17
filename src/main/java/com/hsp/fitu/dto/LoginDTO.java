package com.hsp.fitu.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginDTO {
    private String token;
    private boolean isNewUser;
    private Long userId;
}
