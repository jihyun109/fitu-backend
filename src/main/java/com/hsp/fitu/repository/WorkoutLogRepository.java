package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface WorkoutLogRepository extends JpaRepository<WorkoutLogEntity, Long> {
    List<WorkoutLogEntity> findByUserIdAndRecordedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}