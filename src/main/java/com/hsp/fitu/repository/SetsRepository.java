package com.hsp.fitu.repository;

import com.hsp.fitu.entity.SetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetsRepository extends JpaRepository<SetsEntity, Long> {
    List<SetsEntity> findBySessionExerciseId(long sessionExerciseId);
}