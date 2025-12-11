package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutCalendarFullDTO;
import com.hsp.fitu.dto.WorkoutCalendarSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutCalendarService {
    List<WorkoutCalendarSummaryDTO> getFullWorkoutCalendar(Long userId, int year, int month);
    WorkoutCalendarFullDTO getDetailWorkoutCalendar(Long userId, LocalDate date);
}