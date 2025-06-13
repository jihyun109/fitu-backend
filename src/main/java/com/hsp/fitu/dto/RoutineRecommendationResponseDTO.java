package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.WorkoutCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class RoutineRecommendationResponseDTO {
    private WorkoutCategory bodyPart;
    private WorkoutWithImageDTO mainWorkout;
    private List<WorkoutWithImageDTO> similarWorkouts;
}
