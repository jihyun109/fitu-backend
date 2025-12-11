package com.hsp.fitu.dto;

public record WorkoutSetRequestDTO(
        int setIndex,
        int weight,
        int reps
) {
}
