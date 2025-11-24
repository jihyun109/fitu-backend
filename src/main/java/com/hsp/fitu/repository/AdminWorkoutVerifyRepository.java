package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutVerificationEntity;
import com.hsp.fitu.entity.enums.WorkoutVerificationRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface AdminWorkoutVerifyRepository extends JpaRepository<WorkoutVerificationEntity, Long> {
    Page<WorkoutVerificationEntity> findAllByStatus(WorkoutVerificationRequestStatus status, Pageable pageable);

    void deleteById(long verifyId);
}
