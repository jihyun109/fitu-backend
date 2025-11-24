package com.hsp.fitu.dto.admin;

import java.util.Date;

public record AdminWorkoutVerifyDetailResponseDTO(
        long id,
        String name,
        Date date,
        String workoutName,
        String videoUrl
) {
}
