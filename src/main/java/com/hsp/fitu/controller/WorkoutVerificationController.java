package com.hsp.fitu.controller;

import com.hsp.fitu.dto.WorkoutVerificationRequestDTO;
import com.hsp.fitu.entity.enums.WorkoutVerificationType;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.WorkoutVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/workout-verification")
public class WorkoutVerificationController {

    private final WorkoutVerificationService workoutVerificationService;

    @PostMapping()
    public ResponseEntity<String> requestWorkoutVerification(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @Valid @RequestPart WorkoutVerificationType workoutVerificationType,
                                                             @RequestPart MultipartFile workoutVerificationVideo) {

        workoutVerificationService.requestWorkoutVerification(WorkoutVerificationRequestDTO.builder()
                .userId(userDetails.getId())
                .workoutVerificationType(workoutVerificationType)
                .workoutVerificationVideo(workoutVerificationVideo)
                .build());

        return ResponseEntity.ok("Workout verification request succeeded.");
    }
}
