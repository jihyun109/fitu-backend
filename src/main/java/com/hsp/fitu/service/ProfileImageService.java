package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.entity.BodyImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileImageService {
    BodyImageMainResponseDTO getMainProfileImage(long userId);

    List<BodyImageEntity> getProfileImages(long userId);

    String uploadProfileImage(MultipartFile file, long userId);
}
