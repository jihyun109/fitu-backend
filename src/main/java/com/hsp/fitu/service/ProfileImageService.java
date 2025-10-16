package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.entity.BodyImageEntity;

import java.util.List;

public interface ProfileImageService {
    BodyImageMainResponseDTO getMainBodyImage(long userId);
    List<BodyImageEntity> getBodyImages(long userId);
}
