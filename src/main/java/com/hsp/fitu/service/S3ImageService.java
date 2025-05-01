package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageDeleteRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface S3ImageService {
    String upload(MultipartFile image, long userId);
    void deleteImageFromS3(BodyImageDeleteRequestDTO imageUrl);
}
