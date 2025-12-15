package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WorkoutService {
    List<OldRoutineResponseDTO> suggestRoutine(RoutineRecommendationRequestDTO requestDTO);

    WorkoutSelectResponseDTO recommendRoutine(RoutineRecommendationRequestDTO requestDTO);

    WorkoutSelectResponseDTO selectRoutine(WorkoutCustomRequestDTO requestDTO);

    List<WorkoutGifResponseDTO> getWorkoutGifs(WorkoutGifRequestDTO requestDTO);

    void updateWorkoutImage(long workoutId, MultipartFile image);

    void updateWorkoutGif(long workoutId, MultipartFile gif);
}
