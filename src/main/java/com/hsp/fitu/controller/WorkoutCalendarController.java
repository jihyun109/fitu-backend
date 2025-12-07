package com.hsp.fitu.controller;

import com.hsp.fitu.dto.WorkoutCalendarFullDTO;
import com.hsp.fitu.dto.WorkoutCalendarSummaryDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.WorkoutCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workout/calendar")
@RequiredArgsConstructor
public class WorkoutCalendarController {

    private final WorkoutCalendarService workoutCalendarService;

    @Operation(summary = "마이페이지 해당 날짜 운동 조회 by 조민기")
    @GetMapping("/full")
    public ResponseEntity<List<WorkoutCalendarSummaryDTO>> getFullWorkoutCalendar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(
                workoutCalendarService.getFullWorkoutCalendar(userId, year, month)
        );
    }

    @Operation(summary = "마이페이지_운동기록 상세 by 조민기")
    @GetMapping("/details")
    public ResponseEntity<WorkoutCalendarFullDTO> getDetailWorkoutCalendar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam LocalDate date) {

        Long userId = userDetails.getId();
        return ResponseEntity.ok(
                workoutCalendarService.getDetailWorkoutCalendar(userId, date));

    }
}
