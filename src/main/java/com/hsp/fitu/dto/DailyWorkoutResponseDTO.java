package com.hsp.fitu.dto;

import java.time.LocalDate;
import java.util.List;

public record DailyWorkoutResponseDTO(
        LocalDate date,
        List<SessionResponseDTO> sessions
) {}
