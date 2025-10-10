package com.hsp.fitu.dto;

import java.util.List;

public record SessionExerciseResponseDTO(
        long workoutId,
        int orderIndex,
        List<SetResponseDTO> setResponseDTOs
) {}
