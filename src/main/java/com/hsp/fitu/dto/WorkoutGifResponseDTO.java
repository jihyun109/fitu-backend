package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WorkoutGifResponseDTO {
    private Workout workoutName;
    private String gif;
}
