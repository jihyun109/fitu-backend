package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSelectResponseDTO {
    List<SelectedWorkout> selectedWorkouts;
}
