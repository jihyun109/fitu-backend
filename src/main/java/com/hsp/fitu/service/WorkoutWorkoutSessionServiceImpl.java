package com.hsp.fitu.service;

import com.hsp.fitu.dto.DailyWorkoutResponseDTO;
import com.hsp.fitu.dto.SessionExerciseResponseDTO;
import com.hsp.fitu.dto.SessionResponseDTO;
import com.hsp.fitu.entity.SessionExercisesEntity;
import com.hsp.fitu.entity.SessionsEntity;
import com.hsp.fitu.repository.SessionExercisesRepository;
import com.hsp.fitu.repository.SessionsRepository;
import com.hsp.fitu.repository.SetsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutWorkoutSessionServiceImpl implements WorkoutSessionService {
    private final SessionsRepository sessionsRepository;
    private final SessionExercisesRepository sessionExercisesRepository;
    private final SetsRepository setsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DailyWorkoutResponseDTO> getMonthlyWorkouts(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = startDate.withDayOfMonth(startDate.lengthOfMonth()).atTime(23,59,59);

        List<SessionsEntity> sessionsEntityList = sessionsRepository.findByUserIdCreatedAtBetween(userId, start, end);

        Map<LocalDate, List<SessionsEntity>> groupedByDate = sessionsEntityList.stream()
                .collect(Collectors.groupingBy(sessionsEntity ->sessionsEntity.getCreatedAt().toLocalDate()));

        return groupedByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<SessionResponseDTO> sessionResponseDTOs = entry.getValue().stream()
                            .map(this::convertToSessionResponse)
                            .toList();
                })
                .sorted(Comparator.comparing(DailyWorkoutResponseDTO::date))
                .toList();
    }

    private SessionResponseDTO convertToSessionResponse(SessionsEntity sessionsEntity) {
        List<SessionExercisesEntity> exercisesEntities = sessionExercisesRepository.findBySessionId(sessionsEntity.getId())

        List<SessionExerciseResponseDTO> exerciseResponseDTOs = exercisesEntities.stream().map(ex ->)


    }
}
