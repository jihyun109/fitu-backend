package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutCalendarFullDTO;
import com.hsp.fitu.dto.WorkoutCalendarDetailDTO;
import com.hsp.fitu.dto.WorkoutCalendarSummaryDTO;
import com.hsp.fitu.dto.WorkoutSetDetailDTO;
import com.hsp.fitu.entity.*;
import com.hsp.fitu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutCalendarServiceImpl implements WorkoutCalendarService {
    private final SessionsRepository sessionsRepository;
    private final SessionExercisesRepository sessionExercisesRepository;
    private final WorkoutNewRepository workoutNewRepository;
    private final SetsRepository setsRepository;
    private final WorkoutCategoryRepository workoutCategoryRepository;
    private final MediaFilesRepository mediaFilesRepository;

    @Override
    public List<WorkoutCalendarSummaryDTO> getFullWorkoutCalendar(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<SessionsEntity> sessionsEntities =
                sessionsRepository.findByUserIdAndStartTimeBetween(
                        userId,
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)
                );

        return sessionsEntities.stream()
                .map(sessions -> {
                    String imageUrl = null;

                    if (sessions.getExerciseImageId() != null) {
                        imageUrl = mediaFilesRepository.findById(sessions.getExerciseImageId())
                                .map(MediaFilesEntity::getUrl)
                                .orElse(null);
                    }

                    List<SessionExercisesEntity> sessionExEntities =
                            sessionExercisesRepository.findBySessionIdOrderByOrderIndex(sessions.getId());

                    Long categoryId = null;
                    String categoryName = null;

                    if (!sessionExEntities.isEmpty()) {
                        WorkoutEntity workoutEntity = workoutNewRepository.findById(sessionExEntities.get(0).getWorkoutId())
                                .orElse(null);

                        if (workoutEntity != null) {
                            categoryId = workoutEntity.getCategoryId();

                            categoryName = workoutCategoryRepository.findById(categoryId)
                                    .map(cat -> cat.getName().name())
                                    .orElse(null);
                        }
                    }

                    return new WorkoutCalendarSummaryDTO(
                            sessions.getStartTime().toLocalDate(),
                            categoryId,
                            categoryName,
                            imageUrl
                    );
                })
                .toList();
    }

    @Override
    public WorkoutCalendarFullDTO getDetailWorkoutCalendar(Long userId, LocalDate date) {
        SessionsEntity sessionsEntity = sessionsRepository.findFirstByUserIdAndStartTimeBetween(
                userId,
                date.atStartOfDay(),
                date.atTime(23,59,59)
        )
                .orElseThrow(() -> new RuntimeException("운동기록이 존재하지 않습니다."));

        List<SessionExercisesEntity> exercisesEntities =
                sessionExercisesRepository.findBySessionIdOrderByOrderIndex(sessionsEntity.getId());

        List<WorkoutCalendarDetailDTO> exerciseDTOs = exercisesEntities.stream()
                .map(ex -> {
                    WorkoutEntity workoutEntity = workoutNewRepository.findById(ex.getWorkoutId())
                            .orElseThrow();

                    List<SetsEntity> setsEntities =
                            setsRepository.findBySessionExerciseIdOrderBySetIndex(ex.getId());

                    List<WorkoutSetDetailDTO> setDetailDTOs = setsEntities.stream()
                            .map(s -> new WorkoutSetDetailDTO(
                                    s.getSetIndex(),
                                    s.getWeight(),
                                    s.getReps()
                            ))
                            .toList();

                    return new WorkoutCalendarDetailDTO(
                            workoutEntity.getName(),
                            workoutEntity.getImageUrl(),
                            setDetailDTOs
                    );
                }).toList();

        String todayImageUrl = null;
        if (sessionsEntity.getExerciseImageId() != null) {
            todayImageUrl = mediaFilesRepository.findById(sessionsEntity.getExerciseImageId())
                    .map(MediaFilesEntity::getUrl)
                    .orElse(null);
        }

        int totalMinutes =
                (int) java.time.Duration.between(
                        sessionsEntity.getStartTime(),
                        sessionsEntity.getEndTime()
                ).toMinutes();

        return new WorkoutCalendarFullDTO(
                sessionsEntity.getStartTime().toLocalDate(),
                totalMinutes,
                todayImageUrl,
                exerciseDTOs
        );
    }
}