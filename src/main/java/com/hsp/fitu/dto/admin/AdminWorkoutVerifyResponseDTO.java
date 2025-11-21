package com.hsp.fitu.dto.admin;

import java.util.Date;

public record AdminWorkoutVerifyResponseDTO(
        String name,
        Date date,
        String workoutName,
) {}
