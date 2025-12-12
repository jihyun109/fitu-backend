package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.MediaFilesEntity;
import com.hsp.fitu.entity.SessionExercisesEntity;
import com.hsp.fitu.entity.SessionsEntity;
import com.hsp.fitu.entity.SetsEntity;
import com.hsp.fitu.entity.enums.MediaCategory;
import com.hsp.fitu.entity.enums.Workout;
import com.hsp.fitu.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WorkoutSessionServiceImpl implements WorkoutSessionService {
    private final SessionRespository sessionRepository;
    private final MediaFilesRepository mediaFilesRepository;
    private final WorkoutNewRepository workoutNewRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final SetsRepository setsRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public SessionStartResponseDTO startSession(Long userId) {
        SessionsEntity sessions = SessionsEntity.builder()
                .userId(userId)
                .startTime(LocalDateTime.now())
                .exerciseImageId(null)
                .build();

        SessionsEntity saved = sessionRepository.save(sessions);

        return new SessionStartResponseDTO(saved.getId(), saved.getStartTime());
    }

    @Override
    @Transactional
    public SessionEndResponseDTO endSession(Long userId, SessionEndRequestDTO requestDTO, MultipartFile image) {
        SessionsEntity sessions = sessionRepository.findById(requestDTO.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

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

        sessions.setEndTime(LocalDateTime.now());
        sessions.setExerciseImageId(mediaId);
        sessionRepository.save(sessions);

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

    private Long resolveWorkoutIdByName(String workoutNameStr) {
        if (workoutNameStr == null) {
            throw new IllegalArgumentException("workoutName is null");
        }

        String normalized = workoutNameStr.trim().replace(" ", "_").toUpperCase();
        try {
            Workout enumValue = Workout.valueOf(normalized);

            Long workoutId = workoutNewRepository.findIdByName(enumValue);

            if (workoutId == null) {
                throw new EntityNotFoundException("Workout not for for name");
            }

            return workoutId;

        }catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Invalid workout name: " + workoutNameStr);
        }
    }
}