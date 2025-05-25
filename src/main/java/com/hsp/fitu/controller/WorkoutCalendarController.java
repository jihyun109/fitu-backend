package com.hsp.fitu.controller;

import com.hsp.fitu.dto.WorkoutCalendarFullDTO;
import com.hsp.fitu.service.WorkoutCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workout/calendar")
@RequiredArgsConstructor
public class WorkoutCalendarController {

    private final WorkoutCalendarService workoutCalendarService;

    @GetMapping("/full")
    public ResponseEntity<List<WorkoutCalendarFullDTO>> getFullWorkoutCalendar(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(
                workoutCalendarService.getFullWorkoutCalendar(userId, year, month)
        );
    }
}
