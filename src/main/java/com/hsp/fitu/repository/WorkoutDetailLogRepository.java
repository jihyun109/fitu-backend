package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutDetailLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutDetailLogRepository extends JpaRepository<WorkoutDetailLogEntity, Long> {
    List<WorkoutDetailLogEntity> findByWorkoutLogId(Long workoutLogId);
}
