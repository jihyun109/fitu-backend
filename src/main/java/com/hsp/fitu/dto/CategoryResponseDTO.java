package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.WorkoutCategory;

import java.util.List;

public record CategoryResponseDTO(
        WorkoutCategory category,
        List<WorkoutCustomDetailResponseDTO> data
) {
}
