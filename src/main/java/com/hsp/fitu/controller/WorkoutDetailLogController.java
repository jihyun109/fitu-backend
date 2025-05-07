package com.hsp.fitu.controller;

import com.hsp.fitu.dto.WorkoutLogRequestDTO;
import com.hsp.fitu.service.WorkoutDetailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-detail-logs")
@RequiredArgsConstructor
public class WorkoutDetailLogController {

    private final WorkoutDetailLogService workoutDetailLogService;

    @PostMapping
    public ResponseEntity<String> createWorkoutDetailLog(
            @RequestParam("userId") long userId,
            @RequestBody WorkoutLogRequestDTO requestDTO) {

        workoutDetailLogService.saveWorkoutDetailLog(userId, requestDTO);
        return ResponseEntity.ok("운동 기록 저장 완료");
    }
}

//        for (WorkoutDetailLogRequestDTO dto : workoutList) {
//        System.out.println("Weight: " + dto.getWeight()
//                    + ", Sets: " + dto.getNumOfSets()
//                    + ", Reps: " + dto.getRepsPerSet());
//        }
//workoutlog, detail entity에 둘다 저장.