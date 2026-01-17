package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageDeleteRequestDTO;
import com.hsp.fitu.entity.enums.MediaCategory;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.repository.MediaFilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;
    private final MediaFilesRepository mediaFilesRepository;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public String upload(MultipartFile file, MediaCategory mediaCategory) {
        // 1. 파일 비어있는지 검증
        validateFileExists(file);

        // 2. 확장자 체크
        validateExtension(file);

        String folderName = getFolderName(mediaCategory);   // 폴더 이름
        // 미디어 파일 S3에 업로드 & get media file url
        return this.uploadFileToS3(file, folderName);
    }

    @Override
    public String uploadFileToS3(MultipartFile file, String folder) {
        try {
            return this.uploadToS3(file, folder);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private String uploadToS3(MultipartFile file, String folder) throws IOException {
        // 저장할 파일 이름 생성
        String s3FileName = generateFileName(file, folder);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3FileName)
                .acl(ObjectCannedACL.PUBLIC_READ) // Public 권한
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        }

        // todo : 정적 팩토리 메서드나 별도 유틸로 분리
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3FileName);
    }

    @Override
    @Transactional
    public void deleteImageFromS3(BodyImageDeleteRequestDTO dto) {
        String imageUrl = dto.getImageUrl();
        String key = getKeyFromImageAddress(imageUrl);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            // DB에서도 삭제
            mediaFilesRepository.deleteByUrl(imageUrl);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.S3_DELETE_FAILED);
        }
    }

    // --- Helper Methods ---

    private String getFolderName(MediaCategory mediaCategory) {
        return switch (mediaCategory) {
            case PROFILE_IMAGE -> "profile_img/";
            case WORKOUT_VERIFICATION -> "workout_verification_video/";
            case WORKOUT_COMPLETE -> "today_workout_complete/";
            case INQUIRY -> "inquiry/";
        };
    }

    private void validateFileExists(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_FILE);
        }
    }

    private void validateExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new BusinessException(ErrorCode.MISSING_FILE_EXTENSION);
        }
        // 확장자 추출 후 검증 로직 추가 가능
    }

    private String getKeyFromImageAddress(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE_FILE);
        }
    }

    private String generateFileName(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename(); //원본 파일 명
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자
        return folder + UUID.randomUUID() + ext;
    }
}
