package com.hsp.fitu.controller;

import com.hsp.fitu.dto.CategoryResponseDTO;
import com.hsp.fitu.dto.WorkoutCustomDetailResponseDTO;
import com.hsp.fitu.service.WorkoutCustomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/workouts")
public class WorkoutCustomController {
    private final WorkoutCustomService workoutCustomService;

    @Operation(summary = "운동 커스텀 하기 by 조민기")
    @GetMapping("/custom")
    public ResponseEntity<CategoryResponseDTO> getCustomWorkouts(
            @RequestParam String category
    ) {
        return ResponseEntity.ok(workoutCustomService.getWorkoutsByCategory(category));
    }

    @Operation(summary = "커스텀하기 검색 by 조민기")
    @GetMapping
    public List<WorkoutCustomDetailResponseDTO> searchWorkoutCustom (
            @RequestParam String keyword
    ) {
        return workoutCustomService.searchWorkout(keyword);
    }
}