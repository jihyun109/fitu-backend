package com.hsp.fitu.dto;

import java.time.LocalDate;

public record WorkoutCalendarSummaryDTO(
        LocalDate date,
        Long categoryId,
        String categoryName,
        String dailyPhoto
) {}
