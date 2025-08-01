package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageDeleteRequestDTO;
import com.hsp.fitu.entity.BodyImageEntity;
import com.hsp.fitu.error.customExceptions.EmptyFileException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.error.customExceptions.S3UploadFailException;
import com.hsp.fitu.repository.BodyImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageServiceImpl implements S3ImageService {

    private final S3Client s3Client;
    private final BodyImageRepository bodyImageRepository;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public String upload(MultipartFile image, long userId) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new EmptyFileException(ErrorCode.EMPTY_FILE);
        }

        String imageUrl = this.uploadImage(image, "body-image");
        bodyImageRepository.save(BodyImageEntity.builder()
                .url(imageUrl)
                .userId(userId)
                .build());

        return imageUrl;
    }

    @Override
    public String uploadImage(MultipartFile image, String folder) {
        this.validateImageFileExtention(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image, folder);
        } catch (IOException e) {
            throw new EmptyFileException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new EmptyFileException(ErrorCode.MISSING_FILE_EXTENSION);
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new EmptyFileException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadImageToS3(MultipartFile image, String folder) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String s3FileName = folder + UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = image.getBytes();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3FileName)
                .acl(ObjectCannedACL.PUBLIC_READ) // Public 권한
                .contentType(image.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
        } catch (Exception e) {
            log.error("S3 Upload failed: {}", e.getMessage(), e);
            throw new S3UploadFailException(ErrorCode.S3_UPLOAD_FAILED);
        }

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + s3FileName;
    }

    @Override
    public void deleteImageFromS3(BodyImageDeleteRequestDTO dto) {
        String imageUrl = dto.getImageUrl();
        String key = getKeyFromImageAddress(imageUrl);
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            bodyImageRepository.deleteByUrl(imageUrl);
        } catch (Exception e) {
            throw new EmptyFileException(ErrorCode.S3_DELETE_FAILED);
        }
    }

    private String getKeyFromImageAddress(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new EmptyFileException(ErrorCode.INVALID_IMAGE_FILE);
        }
    }
}
