package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutEntity;
import com.hsp.fitu.entity.enums.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface WorkoutRepository extends JpaRepository<WorkoutEntity, Long> {
    Optional<WorkoutEntity> findByName(Workout name);

    List<WorkoutEntity> findAllByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT w FROM WorkoutEntity w WHERE w.categoryId = :categoryId AND w.name <> :mainWorkoutName")
    List<WorkoutEntity> findSimilarWorkouts(@Param("mainWorkoutName") Workout mainWorkoutName, @Param("categoryId") Long categoryId);
}
