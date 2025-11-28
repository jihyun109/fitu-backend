package com.hsp.fitu.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSaveInfoResponseDTO {
    private String newToken;
}
