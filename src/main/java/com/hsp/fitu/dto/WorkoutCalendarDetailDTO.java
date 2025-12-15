package com.hsp.fitu.dto;

import java.util.List;

public record WorkoutCalendarDetailDTO(
        String name,
        String categoryName,
        String WorkoutImage,
        List<WorkoutSetDetailDTO> sets
) {}
