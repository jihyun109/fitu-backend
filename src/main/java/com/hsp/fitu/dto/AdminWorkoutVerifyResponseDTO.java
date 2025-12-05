package com.hsp.fitu.dto;

import java.util.Date;

public record AdminWorkoutVerifyResponseDTO(
        long id,
        String name,
        Date date,
        String workoutName
) {}
