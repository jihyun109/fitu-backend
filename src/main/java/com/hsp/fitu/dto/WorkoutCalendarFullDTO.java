package com.hsp.fitu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class WorkoutCalendarFullDTO {
    private LocalDate date;
//    private List<Long> categoryIds;
    private List<WorkoutCalendarDetailDTO> details;
}
