package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.WorkoutCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class RoutineRecommendationRequestDTO {
    private List<WorkoutCategory> workoutCategoryList;
}
