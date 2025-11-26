package com.hsp.fitu.dto;

public record AdminAddExerciseRequestDTO(
        Long workoutId,
        String workoutName,
        Long categoryId,
        String categoryName,
        Long equipmentId,
        String equipmentName,
        String imageUrl,
        String gifUrl
) {}
