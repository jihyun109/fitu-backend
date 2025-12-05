package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserProfileImageResponseDto {
    private final String url;
    private final boolean profileVisible;
}
