package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.*;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final MediaFilesRepository mediaFilesRepository;
    private final WorkoutNewRepository workoutNewRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final WorkoutBulkRepository workoutBulkRepository;

    @Override
    @Transactional
    public SessionEndResponseDTO saveSessionData(Long userId, SessionEndRequestDTO requestDTO, String imageUrl) {
        // 1. 세션 및 이미지 정보 저장
        SessionsEntity savedSession = saveSessionWithImage(userId, requestDTO.totalMinutes(), imageUrl);

        // 2. 운동 종목 ID 매핑 조회
        Map<String, Long> workoutMap = findWorkoutIdsByName(requestDTO);

        // 3. Exercise 엔티티 생성 및 저장 (Sets DTO 매핑 정보 포함)
        ExerciseSaveResult exerciseResult = saveExercises(savedSession.getId(), requestDTO.exercises(), workoutMap);

        // 4. Sets 엔티티 생성 및 Bulk Insert
        bulkInsertSets(exerciseResult);

        return new SessionEndResponseDTO(savedSession.getId(), "OK");
    }

    private SessionsEntity saveSessionWithImage(Long userId, Integer totalMinutes, String imageUrl) {
        SessionsEntity session = SessionsEntity.builder()
                .userId(userId)
                .startTime(calculateStartTime(totalMinutes))
                .build();

        if (imageUrl != null) {
            MediaFilesEntity media = mediaFilesRepository.save(
                    MediaFilesEntity.builder().uploaderId(userId).url(imageUrl).build()
            );
            session.setExerciseImageId(media.getId());
        }
        return sessionRepository.save(session);
    }

    private Map<String, Long> findWorkoutIdsByName(SessionEndRequestDTO requestDTO) {
        List<String> names = requestDTO.exercises().stream()
                .map(SessionExerciseRequestDTO::workoutName)
                .distinct()
                .toList();

        return workoutNewRepository.findByWorkoutNameIn(names).stream()
                .collect(Collectors.toMap(WorkoutEntity::getWorkoutName, WorkoutEntity::getId));
    }

    private ExerciseSaveResult saveExercises(Long sessionId, List<SessionExerciseRequestDTO> exerciseDtos, Map<String, Long> workoutMap) {
        List<SessionExercisesEntity> entities = new ArrayList<>();
        Map<SessionExercisesEntity, List<WorkoutSetRequestDTO>> setsMap = new HashMap<>();

        for (SessionExerciseRequestDTO exDto : exerciseDtos) {
            Long workoutId = workoutMap.get(exDto.workoutName());

            SessionExercisesEntity entity = SessionExercisesEntity.builder()
                    .sessionId(sessionId)
                    .workoutId(workoutId)
                    .orderIndex(exDto.orderIndex())
                    .build();

            entities.add(entity);
            setsMap.put(entity, exDto.sets());
        }

        List<SessionExercisesEntity> savedExercises = sessionExerciseRepository.saveAll(entities);
        return new ExerciseSaveResult(savedExercises, setsMap);
    }

    private void bulkInsertSets(ExerciseSaveResult exerciseResult) {
        List<SetsEntity> allSets = new ArrayList<>();

        for (SessionExercisesEntity savedExercise : exerciseResult.savedExercises()) {
            List<WorkoutSetRequestDTO> setDtos = exerciseResult.setsMap().get(savedExercise);

            for (WorkoutSetRequestDTO setDto : setDtos) {
                allSets.add(SetsEntity.builder()
                        .sessionExerciseId(savedExercise.getId())
                        .setIndex(setDto.setIndex())
                        .weight(setDto.weight())
                        .reps(setDto.reps())
                        .build());
            }
        }
        workoutBulkRepository.bulkInsertSets(allSets);
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

    private LocalDateTime calculateStartTime(Integer totalMinutes) {
        if (totalMinutes <= 0) throw new BusinessException(ErrorCode.INVALID_TOTAL_MIN);
        return LocalDateTime.now().minusMinutes(totalMinutes);
    }

    // 클래스 내부에 선언된 private record
    private record ExerciseSaveResult(
            List<SessionExercisesEntity> savedExercises,
            Map<SessionExercisesEntity, List<WorkoutSetRequestDTO>> setsMap
    ) {
    }
}