package com.hsp.fitu.service;

import com.hsp.fitu.dto.ProfileImageResponseDTO;
import com.hsp.fitu.dto.ProfileImagesResponseDTO;
import com.hsp.fitu.entity.MediaFilesEntity;
import com.hsp.fitu.entity.enums.MediaCategory;
import com.hsp.fitu.entity.enums.MediaType;
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
    private final MediaFilesRepository mediaFilesRepository;
    private final UserRepository userRepository;

    @Override
    public ProfileImageResponseDTO getMainProfileImage(long userId) {

        return mediaFilesRepository.findMainProfileImageByUserId(userId);
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
                .category(MediaCategory.PROFILE_IMAGE)
                .build());
        Long mediaFileId = mediaFilesEntity.getId();

        // 사용자 프로필 사진 id 수정
        userRepository.updateProfileImgId(userId, mediaFileId);
        return url;
    }
}
