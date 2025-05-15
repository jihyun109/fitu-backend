package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RoutineRecommendationResponseDTO {
    private Workout mainWorkout;
    private List<Workout> similarWorkouts;
}
