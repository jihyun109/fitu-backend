package com.hsp.fitu.repository;

import com.hsp.fitu.entity.WorkoutEntity;
import com.hsp.fitu.entity.enums.Workout;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface WorkoutRepository extends JpaRepository<WorkoutEntity, Long> {
    Optional<WorkoutEntity> findByName(Workout name);

    @Query(value = """
    SELECT w.name
    FROM workouts w
    JOIN workout_categories c ON w.category_id = c.id
    WHERE c.id = :categoryId
    """, nativeQuery = true)
    List<Workout> findNamesByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT w.name FROM WorkoutEntity w WHERE w.categoryId = :categoryId AND w.name <> :mainWorkoutName")
    List<Workout> findSimilarWorkouts(@Param("mainWorkoutName") Workout mainWorkoutName, @Param("categoryId") Long categoryId);

}
