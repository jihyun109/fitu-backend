package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SelectedWorkout {
    private Long workoutId;
    private String workoutName;
    private String workoutDescription;
    private String workoutImgUrl;
    private String workoutGifUrl;
}
