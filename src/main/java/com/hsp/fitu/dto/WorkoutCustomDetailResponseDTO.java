package com.hsp.fitu.dto;

public record WorkoutCustomDetailResponseDTO(
        Long workoutId,
        String workoutName,
        String description,
        String imageUrl
) {}
