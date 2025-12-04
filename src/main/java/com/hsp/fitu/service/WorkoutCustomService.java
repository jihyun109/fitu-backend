package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;

import java.util.List;

public interface WorkoutCustomService {
    List<WorkoutCustomDetailResponseDTO> getWorkoutsByCategory(Long categoryId);
}
