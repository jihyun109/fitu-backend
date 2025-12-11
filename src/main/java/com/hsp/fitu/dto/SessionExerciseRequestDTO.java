package com.hsp.fitu.dto;

import java.util.List;

public record SessionExerciseRequestDTO(
        String workoutName,
        int orderIndex,
        List<WorkoutSetRequestDTO> sets
) {
}
