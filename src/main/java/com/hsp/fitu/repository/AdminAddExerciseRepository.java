package com.hsp.fitu.repository;

import com.hsp.fitu.entity.ExerciseEquipmentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAddExerciseRepository extends JpaRepository<ExerciseEquipmentsEntity, Long> {
}
