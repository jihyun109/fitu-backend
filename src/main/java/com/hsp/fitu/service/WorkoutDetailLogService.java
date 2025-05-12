package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutLogRequestDTO;

public interface WorkoutDetailLogService {
    void saveWorkoutDetailLog(Long userId, WorkoutLogRequestDTO requestDTO);  //파라미터 수정
}