package com.hsp.fitu.service;

import com.hsp.fitu.dto.RoutineRecommendationRequestDTO;
import com.hsp.fitu.dto.RoutineRecommendationResponseDTO;
import com.hsp.fitu.dto.WorkoutGifRequestDTO;
import com.hsp.fitu.dto.WorkoutGifResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WorkoutService {
    List<RoutineRecommendationResponseDTO> suggestRoutine(RoutineRecommendationRequestDTO requestDTO);
    List<WorkoutGifResponseDTO> getWorkoutGifs(WorkoutGifRequestDTO requestDTO);
    void updateWorkoutImage(long workoutId, MultipartFile image);
    void updateWorkoutGif(long workoutId, MultipartFile gif);
}
