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
    WHERE c.name = :categoryName
    """, nativeQuery = true)
    List<Workout> findNamesByCategory(@Param("categoryName") WorkoutCategory workoutCategory);
}
