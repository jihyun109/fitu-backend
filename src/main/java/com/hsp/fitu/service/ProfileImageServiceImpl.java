package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.dto.ProfileImagesResponseDTO;
import com.hsp.fitu.entity.BodyImageEntity;
import com.hsp.fitu.entity.MediaFilesEntity;
import com.hsp.fitu.entity.enums.MediaCategory;
import com.hsp.fitu.entity.enums.MediaType;
import com.hsp.fitu.repository.BodyImageRepository;
import com.hsp.fitu.repository.MediaFilesRepository;
import com.hsp.fitu.repository.UserRepository;
import com.hsp.fitu.validator.MediaValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileImageServiceImpl implements ProfileImageService {
    private final S3Service s3Service;
    private final MediaValidator mediaValidator;
    private final BodyImageRepository bodyImageRepository;
    private final MediaFilesRepository mediaFilesRepository;
    private final UserRepository userRepository;

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
    public ProfileImagesResponseDTO getProfileImages(long userId) {
        return ProfileImagesResponseDTO.builder()
                .profileImages(mediaFilesRepository.findProfileImgsByUserId(userId)).build();
    }

    @Override
    @Transactional
    public String uploadProfileImage(MultipartFile file, long userId) {
        // 이미지 파일 유효성 검사
        mediaValidator.validateMedia(file, MediaType.IMAGE);

        // S3에 프로필이미지 업로드 & url get
        String url = s3Service.upload(file, MediaCategory.PROFILE_IMAGE);

        // db에 데이터 저장
        MediaFilesEntity mediaFilesEntity = mediaFilesRepository.save(MediaFilesEntity.builder()
                .uploaderId(userId)
                .url(url)
                .build());
        Long mediaFileId = mediaFilesEntity.getId();

        // 사용자 프로필 사진 id 수정
        userRepository.updateProfileImgId(userId, mediaFileId);
        return url;
    }
}
