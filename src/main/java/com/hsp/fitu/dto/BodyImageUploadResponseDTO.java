package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class BodyImageUploadResponseDTO {
    private String imageUrl;
}
