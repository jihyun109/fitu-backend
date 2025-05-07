package com.hsp.fitu.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class WorkoutLogRequestDTO {
    private List<WorkoutDetailLogRequestDTO> workoutList;
}
