package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutCalendarFullDTO;
import com.hsp.fitu.dto.WorkoutCalendarDetailDTO;
import com.hsp.fitu.entity.WorkoutDetailLogEntity;
import com.hsp.fitu.entity.OldWorkoutEntity;
import com.hsp.fitu.entity.WorkoutLogEntity;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.error.customExceptions.WorkoutNotFoundException;
import com.hsp.fitu.repository.WorkoutDetailLogRepository;
import com.hsp.fitu.repository.WorkoutLogRepository;
import com.hsp.fitu.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutCalendarServiceImpl implements WorkoutCalendarService {

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutDetailLogRepository workoutDetailLogRepository;
    private final WorkoutRepository workoutRepository;

    @Override
    public List<WorkoutCalendarFullDTO> getFullWorkoutCalendar(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = startDate.withDayOfMonth(startDate.lengthOfMonth()).atTime(23, 59);

        List<WorkoutLogEntity> logs = workoutLogRepository.findByUserIdAndRecordedAtBetween(userId, start, end);

        return logs.stream().map(log -> {
            List<WorkoutDetailLogEntity> details = workoutDetailLogRepository.findByWorkoutLogId(log.getId());

//            List<Long> categoryIds = details.stream()
//                    .map(d -> workoutRepository.findById(d.getWorkoutId())
//                            .map(WorkoutEntity::getCategoryId).orElse(null))
//                    .filter(Objects::nonNull)
//                    .distinct()
//                    .toList();

            List<WorkoutCalendarDetailDTO> detailDTOs = details.stream().map(d -> {
                OldWorkoutEntity workoutEntity = workoutRepository.findById(d.getWorkoutId())
                        .orElseThrow(() -> new WorkoutNotFoundException(ErrorCode.WORKOUT_NOT_FOUND));

                return new WorkoutCalendarDetailDTO(
                        workoutEntity.getName(),
                        workoutEntity.getCategoryId(),
                        d.getNumOfSets(),
                        d.getWeight(),
                        d.getRepsPerSet()
                );
            }).toList();

            return new WorkoutCalendarFullDTO(log.getRecordedAt().toLocalDate(), /*categoryIds,*/ detailDTOs);
        }).toList();
    }
}