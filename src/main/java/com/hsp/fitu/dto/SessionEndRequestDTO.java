package com.hsp.fitu.dto;

import java.util.List;

public record SessionEndRequestDTO(
        Integer totalMinutes,
        List<SessionExerciseRequestDTO> exercises
) {}