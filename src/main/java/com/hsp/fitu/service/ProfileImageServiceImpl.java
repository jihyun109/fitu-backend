package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.entity.BodyImageEntity;
import com.hsp.fitu.entity.enums.MediaCategory;
import com.hsp.fitu.entity.enums.MediaType;
import com.hsp.fitu.repository.BodyImageRepository;
import com.hsp.fitu.validator.MediaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileImageServiceImpl implements ProfileImageService {
    private final S3Service s3Service;
    private final MediaValidator mediaValidator;
    private final BodyImageRepository bodyImageRepository;

    @Override
    public BodyImageMainResponseDTO getMainProfileImage(long userId) {
        BodyImageEntity entity = bodyImageRepository.findFirstUrlByUserIdOrderByRecordedAtDesc(userId);
        if (entity == null) {
            return new BodyImageMainResponseDTO("https://fitu-bucket.s3.ap-northeast-2.amazonaws.com/fitu_default_image.png");
        }
        String imageUrl = entity.getUrl();
        return new BodyImageMainResponseDTO(imageUrl);
    }

    @Override
    public List<BodyImageEntity> getProfileImages(long userId) {
        return bodyImageRepository.findByUserIdOrderByRecordedAtDesc(userId);
    }

    @Override
    public String uploadProfileImage(MultipartFile file, long userId) {
        // 이미지 파일 유효성 검사
        mediaValidator.validateMedia(file, MediaType.IMAGE);

        // S3에 프로필이미지 업로드 & url get
        String url = s3Service.upload(file, MediaCategory.PROFILE_IMAGE);

        // db에 데이터 저장
        bodyImageRepository.save(BodyImageEntity.builder()
                .url(url)
                .userId(userId)
                .build());

        return url;
    }
}
