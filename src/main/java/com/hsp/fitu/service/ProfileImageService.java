package com.hsp.fitu.service;

import com.hsp.fitu.dto.ProfileImageResponseDTO;
import com.hsp.fitu.dto.ProfileImagesResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageService {
    ProfileImageResponseDTO getMainProfileImage(long userId);

    ProfileImagesResponseDTO getProfileImages(long userId);

    String uploadProfileImage(MultipartFile file, long userId);
}
