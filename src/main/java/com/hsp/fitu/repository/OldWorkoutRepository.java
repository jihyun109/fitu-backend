package com.hsp.fitu.repository;

import com.hsp.fitu.entity.OldWorkoutEntity;
import com.hsp.fitu.entity.enums.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OldWorkoutRepository extends JpaRepository<OldWorkoutEntity, Long> {
    OldWorkoutEntity findByName(Workout name);

    List<OldWorkoutEntity> findAllByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT w FROM WorkoutEntity w WHERE w.categoryId = :categoryId AND w.name <> :mainWorkoutName")
    List<OldWorkoutEntity> findSimilarWorkouts(@Param("mainWorkoutName") Workout mainWorkoutName, @Param("categoryId") Long categoryId);


}
