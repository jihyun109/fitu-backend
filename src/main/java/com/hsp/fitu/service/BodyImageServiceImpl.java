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
public class BodyImageServiceImpl implements BodyImageService {
    private final BodyImageRepository bodyImageRepository;

    @Override
    public BodyImageMainResponseDTO getMainBodyImage(long userId) {
        BodyImageEntity entity = bodyImageRepository.findFirstUrlByUserIdOrderByRecordedAtDesc(userId);
        String imageUrl = entity.getUrl();
        return new BodyImageMainResponseDTO(imageUrl);
    }

    @Override
    public List<BodyImageEntity> getBodyImages(long userId) {

        return bodyImageRepository.findByUserIdOrderByRecordedAtDesc(userId);
    }
}
