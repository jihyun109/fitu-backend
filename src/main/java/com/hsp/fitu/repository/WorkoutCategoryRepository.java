package com.hsp.fitu.repository;

import com.hsp.fitu.entity.OldWorkoutCategoryEntity;
import com.hsp.fitu.entity.WorkoutCategoryEntity;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import com.hsp.fitu.repository.projection.WorkoutCategoryNameOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutCategoryRepository extends JpaRepository<OldWorkoutCategoryEntity, Long> {
    List<WorkoutCategoryNameOnly> findAllBy();
    List<OldWorkoutCategoryEntity> findByNameInOrderByPriority(List<WorkoutCategory> workoutCategory);
    Optional<WorkoutCategoryEntity> findByName(WorkoutCategory name);

}
