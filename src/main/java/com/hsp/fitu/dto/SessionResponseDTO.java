package com.hsp.fitu.dto;

import java.util.List;

public record SessionResponseDTO(
        long sessionId,
        long exerciseImageId,
        List<SessionExerciseResponseDTO> sessionExerciseResponseDTOs
) {}
