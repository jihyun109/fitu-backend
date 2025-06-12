package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;
import lombok.Getter;

import java.util.List;

@Getter
public class WorkoutGifRequestDTO {
    private List<Workout> workouts;
}
