package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkoutCalendarDetailDTO {
    private Workout name;
    private Long categoryId;
    private int sets;
    private int weight;
    private int repsPerSet;
}
