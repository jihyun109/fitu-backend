package com.hsp.fitu.controller;

import com.hsp.fitu.dto.WorkoutVerificationRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.WorkoutVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/workout-verification")
public class WorkoutVerificationController {

    private final WorkoutVerificationService workoutVerificationService;

    @PostMapping()
    @Operation(summary = "3대 500 인증 요청 by 장지현")
    public ResponseEntity<String> requestWorkoutVerification(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @RequestPart (value = "request")WorkoutVerificationRequestDTO requestDTO,
                                                             @RequestPart(value = "video", required = false) MultipartFile workoutVerificationVideo) {
        workoutVerificationService.requestWorkoutVerification(requestDTO, workoutVerificationVideo, userDetails.getId());

        return ResponseEntity.ok("Workout verification request succeeded.");
    }
}
