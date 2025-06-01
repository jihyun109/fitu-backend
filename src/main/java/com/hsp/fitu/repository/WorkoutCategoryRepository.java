package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutCategoryEntity;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import com.hsp.fitu.repository.projection.WorkoutCategoryNameOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutCategoryRepository extends JpaRepository<WorkoutCategoryEntity, Long> {
    List<WorkoutCategoryNameOnly> findAllBy();
    List<WorkoutCategoryEntity> findByNameInOrderByPriority(List<WorkoutCategory> workoutCategory);


}
