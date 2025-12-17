package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutVerificationRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface WorkoutVerificationService {
    void requestWorkoutVerification(WorkoutVerificationRequestDTO workoutVerificationRequestDTO, MultipartFile workoutVerificationVideo, long userId);
}
