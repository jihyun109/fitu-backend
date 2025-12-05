package com.hsp.fitu.service;

import com.hsp.fitu.dto.CategoryResponseDTO;
import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;
import com.hsp.fitu.entity.WorkoutCategoryEntity;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import com.hsp.fitu.repository.WorkoutCategoryRepository;
import com.hsp.fitu.repository.WorkoutNewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutCustomServiceImpl implements WorkoutCustomService{
    private final WorkoutCategoryRepository workoutCategoryRepository;
    private final WorkoutNewRepository workoutRepository;

    @Override
    public CategoryResponseDTO getWorkoutsByCategory(String category) {
        WorkoutCategory workoutCategory = WorkoutCategory.valueOf(category.toUpperCase());

        WorkoutCategoryEntity categoryEntity = workoutCategoryRepository.findByName(workoutCategory)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        long categoryId = categoryEntity.getId();

        List<WorkoutCustomDetailResponseDTO> workoutList =
                workoutRepository.findByCategoryId(categoryId);

        return new CategoryResponseDTO (
                categoryEntity.getName(),
                workoutList
        );
    }

    @Override
    public List<WorkoutCustomDetailResponseDTO> searchWorkout(String keyword) {
        return workoutRepository.searchByKeyword(keyword);

    }
}
