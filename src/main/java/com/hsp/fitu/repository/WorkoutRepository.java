package com.hsp.fitu.repository;

import com.hsp.fitu.dto.SelectedWorkout;
import com.hsp.fitu.entity.WorkoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<WorkoutEntity, Long> {
    List<WorkoutEntity> findAllByCategoryId(@Param("categoryId") Long categoryId);

    @Query("""
            SELECT new com.hsp.fitu.dto.SelectedWorkout(
                w.id, w.workoutName, w.workoutDescription, w.imageUrl, w.gifUrl
            )
            FROM WorkoutEntity w
            WHERE w.id IN :ids
            """)
    List<SelectedWorkout> findAllByIds(@Param("ids") List<Long> ids);
}
