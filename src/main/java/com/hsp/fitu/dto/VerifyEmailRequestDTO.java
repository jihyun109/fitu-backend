package com.hsp.fitu.dto;

import lombok.Getter;

@Getter
public class VerifyEmailRequestDTO {
    private String email;
    private String code;
}
