package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutVerificationEntity;
import com.hsp.fitu.entity.enums.WorkoutVerificationRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminWorkoutVerifyRepository extends JpaRepository<WorkoutVerificationEntity, Long> {
    List<WorkoutVerificationEntity> findAllByStatus(WorkoutVerificationRequestStatus status);
}
