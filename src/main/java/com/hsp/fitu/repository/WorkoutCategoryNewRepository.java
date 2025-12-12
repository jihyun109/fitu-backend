package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkoutCategoryNewRepository extends JpaRepository<WorkoutCategoryEntity, Long> {
}
