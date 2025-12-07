package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;

import java.util.List;

public record WorkoutCalendarDetailDTO(
        Workout name,
        String WorkoutImage,
        List<WorkoutSetDetailDTO> sets
) {}
