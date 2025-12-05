package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;

public record WorkoutCustomDetailResponseDTO(
        Long workoutId,
        Workout workoutName,
        String description,
        String imageUrl
) {}
