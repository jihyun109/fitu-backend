package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageDeleteRequestDTO;
import com.hsp.fitu.entity.enums.MediaCategory;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String upload(MultipartFile image, long userId, MediaCategory mediaCategory);

    void deleteImageFromS3(BodyImageDeleteRequestDTO imageUrl);

    String uploadFileToS3(MultipartFile image, String folder);
}
