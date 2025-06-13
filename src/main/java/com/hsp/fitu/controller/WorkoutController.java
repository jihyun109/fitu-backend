package com.hsp.fitu.controller;

import com.hsp.fitu.dto.RoutineRecommendationRequestDTO;
import com.hsp.fitu.dto.RoutineRecommendationResponseDTO;
import com.hsp.fitu.dto.WorkoutGifRequestDTO;
import com.hsp.fitu.dto.WorkoutGifResponseDTO;
import com.hsp.fitu.entity.WorkoutEntity;
import com.hsp.fitu.repository.WorkoutRepository;
import com.hsp.fitu.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/gifs")
    public ResponseEntity<List<WorkoutGifResponseDTO>> getGifUrlsByWorkoutNames(@RequestBody WorkoutGifRequestDTO requestDTO) {
        return ResponseEntity.ok(workoutService.getWorkoutGifs(requestDTO));
    }

    @PatchMapping("/{workoutId}/media")
    public ResponseEntity<String> updateWorkoutMedia(@PathVariable long workoutId, @RequestPart(value = "image", required = false) MultipartFile image, @RequestPart(value = "gif", required = false) MultipartFile gif) {
        workoutService.updateWorkoutImage(workoutId, image);
        workoutService.updateWorkoutGif(workoutId, gif);
        return ResponseEntity.ok("Media for workout ID " + workoutId + " has been updated.");
    }
}
