package com.hsp.fitu.dto;

import java.time.LocalDate;
import java.util.List;

public record WorkoutCalendarFullDTO(
        LocalDate date,
        int totalMinutes,
        String todayImage,
        List<WorkoutCalendarDetailDTO> details
) {}
