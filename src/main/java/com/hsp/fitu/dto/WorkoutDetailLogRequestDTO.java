package com.hsp.fitu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutDetailLogRequestDTO {
    private String workoutName;
    private int weight;
    private int numOfSets;
    private int repsPerSet;

}
