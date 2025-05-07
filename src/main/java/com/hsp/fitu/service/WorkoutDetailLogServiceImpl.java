package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutDetailLogRequestDTO;
import com.hsp.fitu.dto.WorkoutLogRequestDTO;
import com.hsp.fitu.entity.WorkoutDetailLogEntity;
import com.hsp.fitu.entity.WorkoutEntity;
import com.hsp.fitu.entity.WorkoutLogEntity;
import com.hsp.fitu.entity.enums.Workout;
import com.hsp.fitu.repository.WorkoutDetailLogRepository;
import com.hsp.fitu.repository.WorkoutLogRepository;
import com.hsp.fitu.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class WorkoutDetailLogServiceImpl implements WorkoutDetailLogService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutDetailLogRepository workoutDetailLogRepository;

    @Override
    @Transactional
    public void saveWorkoutDetailLog(Long userId, WorkoutLogRequestDTO requestDTO) {
        WorkoutLogEntity workoutLog = WorkoutLogEntity.builder()
                .userId(userId)
                .build();

        WorkoutLogEntity savedLog = workoutLogRepository.save(workoutLog);

        for (WorkoutDetailLogRequestDTO detailDTO : requestDTO.getWorkoutList()) {
            Workout workoutEnum = Workout.valueOf(detailDTO.getWorkoutName().toUpperCase());//enum변환

            WorkoutEntity workoutEntity = workoutRepository.findByName(workoutEnum) //enum이름으로 id 조회
                    .orElseThrow(() -> new IllegalArgumentException("운동 이름이 존재하지 않습니다: " + detailDTO.getWorkoutName()));

            WorkoutDetailLogEntity detail = WorkoutDetailLogEntity.builder()
                    .workoutLogId(savedLog.getId())
                    .workoutId(workoutEntity.getId())
                    .weight(detailDTO.getWeight())
                    .numOfSets(detailDTO.getNumOfSets())
                    .repsPerSet(detailDTO.getRepsPerSet())
                    .build();

            workoutDetailLogRepository.save(detail);
        }
    }
}
//운동명, 무게, 세트, 세트당 횟수 리스트로 받아. 운동명을 번호로 내가 바꿔서 넣어줘야함.
