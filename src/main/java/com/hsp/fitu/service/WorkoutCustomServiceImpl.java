package com.hsp.fitu.service;

import com.hsp.fitu.dto.CategoryResponseDTO;
import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;
import com.hsp.fitu.entity.enums.WorkoutCategory;
import com.hsp.fitu.repository.WorkoutNewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutCustomServiceImpl implements WorkoutCustomService{
    private final WorkoutNewRepository workoutRepository;

    @Override
    public CategoryResponseDTO getWorkoutsByCategory(String category) {
        WorkoutCategory workoutCategory = WorkoutCategory.valueOf(category.toUpperCase());

        List<WorkoutCustomDetailResponseDTO> workoutList =
                workoutRepository.findByCategoryName(workoutCategory);

        return new CategoryResponseDTO (
                workoutCategory,
                workoutList
        );
    }

    @Override
    public List<WorkoutCustomDetailResponseDTO> searchWorkout(String keyword) {
        return workoutRepository.searchByKeyword(keyword);

    }
}
