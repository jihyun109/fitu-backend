package com.hsp.fitu.service;

import com.hsp.fitu.dto.WorkoutCalendarFullDTO;

import java.util.List;

public interface WorkoutCalendarService {
    List<WorkoutCalendarFullDTO> getFullWorkoutCalendar(Long userId, int year, int month);
}