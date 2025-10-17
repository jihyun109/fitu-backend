package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.entity.BodyImageEntity;
import com.hsp.fitu.repository.BodyImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageServiceImpl implements ProfileImageService {
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
}
