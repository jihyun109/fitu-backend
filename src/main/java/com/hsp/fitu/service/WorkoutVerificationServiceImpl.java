package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutVerificationRequestDTO;
import com.hsp.fitu.entity.MediaFilesEntity;
import com.hsp.fitu.entity.WorkoutVerificationEntity;
import com.hsp.fitu.entity.enums.MediaCategory;
import com.hsp.fitu.entity.enums.MediaType;
import com.hsp.fitu.entity.enums.WorkoutVerificationType;
import com.hsp.fitu.repository.MediaFilesRepository;
import com.hsp.fitu.repository.WorkoutVerificationRepository;
import com.hsp.fitu.validator.MediaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class WorkoutVerificationServiceImpl implements WorkoutVerificationService {
    private final MediaValidator mediaValidator;
    private final WorkoutVerificationRepository workoutVerificationRepository;
    private final MediaFilesRepository mediaFilesRepository;
    private final S3Service s3Service;

    @Override
    public void requestWorkoutVerification(WorkoutVerificationRequestDTO workoutVerificationRequestDTO) {
        // 운동 인증 영상 유효성 검사
        validateWorkoutVerificationRequest(workoutVerificationRequestDTO);

        // 동영상 파일 S3에 저장 & url get
        String fileUrl = s3Service.upload(workoutVerificationRequestDTO.getWorkoutVerificationVideo(), MediaCategory.WORKOUT_VERIFICATION_VIDEO);

        Long userId = workoutVerificationRequestDTO.getUserId();

        // db에 동영상 파일 데이터 저장
        MediaFilesEntity mediaFilesEntity = mediaFilesRepository.save(MediaFilesEntity.builder()
                .url(fileUrl)
                .uploaderId(userId)
                .build());

        Long mediaFileId = mediaFilesEntity.getId();    // 저장된 미디어파일의 id
        WorkoutVerificationType workoutVerificationType = workoutVerificationRequestDTO.getWorkoutVerificationType();  // 운동 인증 타입

        // db에 운동 인증 데이터 저장
        workoutVerificationRepository.save(WorkoutVerificationEntity.builder()
                .userId(workoutVerificationRequestDTO.getUserId())
                .mediaId(mediaFileId)
                .workoutVerificationType(workoutVerificationType)
                .build());
    }

    // 운동 영상 유효성 검사
    private void validateWorkoutVerificationRequest(WorkoutVerificationRequestDTO workoutVerificationRequestDTO) {
        MultipartFile video = workoutVerificationRequestDTO.getWorkoutVerificationVideo();  // 운동 인증 영상
        mediaValidator.validateMedia(video, MediaType.VIDEO);
    }
}
