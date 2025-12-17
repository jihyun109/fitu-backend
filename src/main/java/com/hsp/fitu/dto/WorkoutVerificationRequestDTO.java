package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.WorkoutVerificationType;
import lombok.Getter;

@Getter
public class WorkoutVerificationRequestDTO {
    private WorkoutVerificationType workoutVerificationType;
    private int weight;
}
