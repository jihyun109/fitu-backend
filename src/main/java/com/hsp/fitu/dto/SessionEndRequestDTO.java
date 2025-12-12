package com.hsp.fitu.dto;

import java.util.List;

public record SessionEndRequestDTO(
        Long sessionId,
        List<SessionExerciseRequestDTO> exercises
) {}