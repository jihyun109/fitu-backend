package com.hsp.fitu.service;

import com.hsp.fitu.dto.DailyWorkoutResponseDTO;

import java.util.List;

public interface WorkoutSessionService {
    List<DailyWorkoutResponseDTO> getMonthlyWorkouts(Long userId, int year, int month);
}
