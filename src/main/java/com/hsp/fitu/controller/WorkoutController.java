package com.hsp.fitu.controller;

import com.hsp.fitu.dto.RoutineRecommendationRequestDTO;
import com.hsp.fitu.dto.RoutineRecommendationResponseDTO;
import com.hsp.fitu.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workout")
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;

    @PostMapping("/recommendations")
    public ResponseEntity<List<RoutineRecommendationResponseDTO>> recommendWorkouts(@RequestBody RoutineRecommendationRequestDTO requestDTO) {
        return ResponseEntity.ok(workoutService.suggestRoutine(requestDTO));
    }
}
