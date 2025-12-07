package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutNewRepository extends JpaRepository<WorkoutEntity, Long> {

}
