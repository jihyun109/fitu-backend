package com.hsp.fitu.controller;

import com.hsp.fitu.dto.RoutineRecommendationRequestDTO;
import com.hsp.fitu.dto.RoutineRecommendationResponseDTO;
import com.hsp.fitu.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/workout")
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;

    @GetMapping("/recommendataions")
    public ResponseEntity<List<RoutineRecommendationResponseDTO>> recommendWorkouts(@RequestBody RoutineRecommendationRequestDTO requestDTO) {


        return new ResponseEntity<>(null);
    }
}
