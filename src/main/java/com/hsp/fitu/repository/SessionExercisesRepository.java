package com.hsp.fitu.repository;

import com.hsp.fitu.entity.SessionExercisesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionExercisesRepository extends JpaRepository<SessionExercisesEntity, Long> {
    List<SessionExercisesEntity> findBySessionId(long sessionId);
}
