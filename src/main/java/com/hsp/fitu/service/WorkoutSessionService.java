package com.hsp.fitu.service;

import com.hsp.fitu.dto.SessionEndRequestDTO;
import com.hsp.fitu.dto.SessionEndResponseDTO;
import com.hsp.fitu.dto.SessionStartResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import com.hsp.fitu.dto.DailyWorkoutResponseDTO;

import java.util.List;

public interface WorkoutSessionService {
    List<DailyWorkoutResponseDTO> getMonthlyWorkouts(Long userId, int year, int month);

    SessionStartResponseDTO startSession(Long userId);

    SessionEndResponseDTO endSession(Long userId, SessionEndRequestDTO requestDTO, MultipartFile image);
}
