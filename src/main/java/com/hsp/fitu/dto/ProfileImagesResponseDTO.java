package com.hsp.fitu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProfileImagesResponseDTO {
    List<ProfileImage> profileImages;
}
