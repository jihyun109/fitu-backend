package com.hsp.fitu.repository;

import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;
import com.hsp.fitu.entity.WorkoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutNewRepository extends JpaRepository<WorkoutEntity, Long> {
    @Query("""
        SELECT new com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO(
            w.id,
            w.name,
            w.workoutDescription,
            img.url
        )
        FROM WorkoutEntity w
        JOIN WorkoutCategoryEntity c ON w.categoryId = c.id
        LEFT JOIN MediaFilesEntity img ON w.imageId = img.id
        WHERE w.categoryId = :categoryId
    """)
    List<WorkoutCustomDetailResponseDTO> findByCategoryId(long categoryId);

    @Query("""
        SELECT new com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO(
            w.id,
            w.name,
            w.workoutDescription,
            img.url
        )
        FROM WorkoutEntity w
        JOIN WorkoutCategoryEntity c ON w.categoryId = c.id
        LEFT JOIN MediaFilesEntity img ON w.imageId = img.id
        WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(w.workoutDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<WorkoutCustomDetailResponseDTO> searchByKeyword(@Param("keyword") String keyword);
}