package com.hsp.fitu.facade;

import com.hsp.fitu.dto.SessionEndRequestDTO;
import com.hsp.fitu.dto.SessionEndResponseDTO;
import com.hsp.fitu.entity.enums.MediaCategory;
import com.hsp.fitu.service.S3Service;
import com.hsp.fitu.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class SessionFacade {
    private final SessionService sessionService;
    private final S3Service s3Service;

    // 트랜잭션 없음! 순수 오케스트레이션 역할
    public SessionEndResponseDTO endSessionWithImage(Long userId, SessionEndRequestDTO request, MultipartFile image) {

        // 1. 외부 네트워크 I/O (DB 트랜잭션 없이 실행) -> 병목 제거 핵심
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.upload(image, MediaCategory.WORKOUT_COMPLETE);
        }

        // 2. 실제 DB 작업은 트랜잭션이 걸린 Service 위임
        return sessionService.saveSessionData(userId, request, imageUrl);
    }
}
