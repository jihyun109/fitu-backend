package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class RoutineRecommendationResponseDTO {
    private WorkoutWithImageDTO mainWorkout;
    private List<WorkoutWithImageDTO> similarWorkouts;
}
