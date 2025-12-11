package com.hsp.fitu.controller;

import com.hsp.fitu.dto.SessionEndRequestDTO;
import com.hsp.fitu.dto.SessionEndResponseDTO;
import com.hsp.fitu.dto.SessionStartResponseDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.WorkoutSessionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/workouts/sessions")
public class WorkoutSessionController {
    private final WorkoutSessionService workoutSessionService;

    @Operation(summary = "운동 시작 및 시작 시간 입력 by 조민기")
    @PostMapping("/start")
    public ResponseEntity<SessionStartResponseDTO> startSession(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        Long userId = userDetails.getId();
        SessionStartResponseDTO sessionStartResponseDTO = workoutSessionService.startSession(userId);

        return ResponseEntity.ok(sessionStartResponseDTO);
    }

    @Operation(summary = "운동 종료 및 운동 저장 by 조민기")
    @PostMapping("/end")
    public ResponseEntity<SessionEndResponseDTO> endSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("requestDTO") SessionEndRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
            ) {
        Long userId = userDetails.getId();

        SessionEndResponseDTO responseDTO = workoutSessionService.endSession(userId, requestDTO, image);

        return ResponseEntity.ok(responseDTO);
    }
}
