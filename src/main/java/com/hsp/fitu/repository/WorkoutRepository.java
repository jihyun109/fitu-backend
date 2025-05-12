package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutEntity;
import com.hsp.fitu.entity.enums.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface WorkoutRepository extends JpaRepository<WorkoutEntity, Long> {
    Optional<WorkoutEntity> findByName(Workout name);
}
