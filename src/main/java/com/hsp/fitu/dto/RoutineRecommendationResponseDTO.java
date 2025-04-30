package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;

import java.util.List;

public class RoutineRecommendationResponseDTO {
    private Workout mainWorkout;
    private List<Workout> similarWorkouts;
}
