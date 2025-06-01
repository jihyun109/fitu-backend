package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class RoutineRecommendationResponseDTO {
    private Workout mainWorkout;
    private List<Workout> similarWorkouts;
}
