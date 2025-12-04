package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;
import com.hsp.fitu.repository.WorkoutNewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutCustomServiceImpl implements WorkoutCustomService{
    private final WorkoutNewRepository workoutRepository;

    public List<WorkoutCustomDetailResponseDTO> getWorkoutsByCategory(Long categoryId) {
        return workoutRepository.findByCategoryId(categoryId);
    }
}
