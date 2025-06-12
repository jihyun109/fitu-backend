package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.Workout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WorkoutWithImageDTO {
    private Workout workout;
    private String imageUrl;
}
