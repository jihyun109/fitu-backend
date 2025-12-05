package com.hsp.fitu.service;

import com.hsp.fitu.dto.CategoryResponseDTO;
import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;

import java.util.List;

public interface WorkoutCustomService {
    CategoryResponseDTO getWorkoutsByCategory(String category);
    List<WorkoutCustomDetailResponseDTO> searchWorkout(String keyword);
}
