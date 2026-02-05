package com.hsp.fitu.controller;

import com.hsp.fitu.repository.SessionExercisesRepository;
import com.hsp.fitu.repository.SessionRepository;
import com.hsp.fitu.repository.SetsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/internal")
public class TestController {
    private final SessionRepository sessionRepository;
    private final SessionExercisesRepository sessionExercisesRepository;
    private final SetsRepository setsRepository;

    @GetMapping("/counts")
    public Map<String, Long> getCounts() {
        return Map.of(
                "workout_session", sessionRepository.count(),
                "session_exercise", sessionExercisesRepository.count(),
                "exercise_set", setsRepository.count()
        );
    }
}