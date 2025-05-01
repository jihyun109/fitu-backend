package com.hsp.fitu.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3ImageService {
    String upload(MultipartFile image, long userId);
    void deleteImageFromS3(String imageUrl);
}
