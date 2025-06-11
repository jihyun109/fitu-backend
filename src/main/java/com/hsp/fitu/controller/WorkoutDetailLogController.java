package com.hsp.fitu.controller;

import com.hsp.fitu.dto.WorkoutLogRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.WorkoutDetailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-detail-logs")
@RequiredArgsConstructor
public class WorkoutDetailLogController {

    private final WorkoutDetailLogService workoutDetailLogService;

    @PostMapping
    public ResponseEntity<String> createWorkoutDetailLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody WorkoutLogRequestDTO requestDTO) {

        Long userId = userDetails.getId();
        workoutDetailLogService.saveWorkoutDetailLog(userId, requestDTO);
        return ResponseEntity.ok("Workout log saved successfully.");
    }
}
