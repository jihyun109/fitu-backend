package com.hsp.fitu.controller;

import com.hsp.fitu.repository.projection.WorkoutCategoryNameOnly;
import com.hsp.fitu.service.WorkoutCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workout-categories")
@RequiredArgsConstructor
public class WorkoutCategoryController {
    private final WorkoutCategoryService workoutCategoryService;
    @GetMapping
    public ResponseEntity<List<WorkoutCategoryNameOnly>> getWorkoutCategories() {
        return ResponseEntity.ok(workoutCategoryService.getWorkoutCategories());
    }
}
