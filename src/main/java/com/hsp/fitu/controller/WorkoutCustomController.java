package com.hsp.fitu.controller;

import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;
import com.hsp.fitu.service.WorkoutCustomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/workouts")
public class WorkoutCustomController {
    private final WorkoutCustomService workoutCustomService;

    @Operation(summary = "운동 커스텀 하기 by 조민기")
    @GetMapping("/custom")
    public ResponseEntity<List<WorkoutCustomDetailResponseDTO>> getCustomWorkouts(
            @RequestParam long categoryId
    ) {
        return ResponseEntity.ok(workoutCustomService.getWorkoutsByCategory(categoryId));
    }
}