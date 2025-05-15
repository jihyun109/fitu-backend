package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.WorkoutCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class RoutineRecommendationRequestDTO {
    @Valid
    @Size(min = 3, max = 5, message = "운동 부위는 3개 이상 5개 이하로 선택해야 합니다.")
    private List<WorkoutCategory> workoutCategoryList;
}
