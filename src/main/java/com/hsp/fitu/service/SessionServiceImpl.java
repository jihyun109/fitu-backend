package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.*;
import com.hsp.fitu.entity.enums.MediaCategory;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final MediaFilesRepository mediaFilesRepository;
    private final WorkoutNewRepository workoutNewRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final SetsRepository setsRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public SessionEndResponseDTO endSession(Long userId, SessionEndRequestDTO requestDTO, MultipartFile image) {

        Integer totalMinutes = requestDTO.totalMinutes();
        if (totalMinutes <= 0) {
            throw new BusinessException(ErrorCode.INVALID_TOTAL_MIN);
        }

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(totalMinutes);

        SessionsEntity sessions = sessionRepository.save(
                SessionsEntity.builder()
                        .userId(userId)
                        .startTime(startTime)
                        .build()
        );

        Long mediaId = null;

        if (image != null && !image.isEmpty()) {
            String url = s3Service.upload(image, MediaCategory.WORKOUT_COMPLETE);

            MediaFilesEntity mediaFiles = mediaFilesRepository.save(
                    MediaFilesEntity.builder()
                            .uploaderId(userId)
                            .url(url)
                            .build()
            );
            mediaId = mediaFiles.getId();
        }
        sessions.setExerciseImageId(mediaId);

        for (SessionExerciseRequestDTO exerciseRequestDTO : requestDTO.exercises()) {

            long workoutId = resolveWorkoutIdByName(exerciseRequestDTO.workoutName());

            SessionExercisesEntity sessionExercises = SessionExercisesEntity.builder()
                    .sessionId(sessions.getId())
                    .workoutId(workoutId)
                    .orderIndex(exerciseRequestDTO.orderIndex())
                    .build();

            SessionExercisesEntity savedSE = sessionExerciseRepository.save(sessionExercises);

            for (WorkoutSetRequestDTO setRequestDTO : exerciseRequestDTO.sets()) {
                SetsEntity setsEntity = SetsEntity.builder()
                        .sessionExerciseId(savedSE.getId())
                        .setIndex(setRequestDTO.setIndex())
                        .weight(setRequestDTO.weight())
                        .reps(setRequestDTO.reps())
                        .build();

                setsRepository.save(setsEntity);
            }
        }
        return new SessionEndResponseDTO(sessions.getId(), "OK");
    }

    private Long resolveWorkoutIdByName(String workoutName) {
        if (workoutName == null || workoutName.isBlank()) {
            throw new BusinessException(ErrorCode.WORKOUT_NOT_FOUND);
        }

        return workoutNewRepository.findByWorkoutName(workoutName.trim())
                .map(WorkoutEntity::getId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.WORKOUT_NOT_FOUND)
                );
    }
}