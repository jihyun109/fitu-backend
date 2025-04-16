package com.hsp.fitu.service;

import com.hsp.fitu.dto.BodyImageMainResponseDTO;
import com.hsp.fitu.repository.BodyImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BodyImageServiceImpl implements BodyImageService{
    private BodyImageRepository bodyImageRepository;
    @Override
    public BodyImageMainResponseDTO getMainBodyImage(long userId) {
        String imageUrl = bodyImageRepository.findMainImageUrlByUserIdAndOOrderByRecordedAtDesc(userId);
        BodyImageMainResponseDTO bodyImageMainResponseDTO = new BodyImageMainResponseDTO(imageUrl);
        return bodyImageMainResponseDTO;
    }
}
