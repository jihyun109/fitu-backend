package com.hsp.fitu.repository;

import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;
import com.hsp.fitu.entity.WorkoutEntity;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutNewRepository extends JpaRepository<WorkoutEntity, Long> {
    @Query("""
        SELECT new com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO(
            w.id,
            w.workoutName,
            w.workoutDescription,
            w.imageUrl
        )
        FROM WorkoutEntity w
        JOIN WorkoutCategoryEntity c ON w.categoryId = c.id
        WHERE c.name = :categoryName
    """)
    List<WorkoutCustomDetailResponseDTO> findByCategoryName(@Param("categoryName") WorkoutCategory categoryName);

    @Query("""
        SELECT new com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO(
            w.id,
            w.workoutName,
            w.workoutDescription,
            w.imageUrl
        )
        FROM WorkoutEntity w
        WHERE LOWER(w.workoutName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(w.workoutDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<WorkoutCustomDetailResponseDTO> searchByKeyword(@Param("keyword") String keyword);
}