package com.hsp.fitu.controller;

import com.hsp.fitu.dto.SessionEndRequestDTO;
import com.hsp.fitu.dto.SessionEndResponseDTO;
import com.hsp.fitu.facade.SessionFacade;
import com.hsp.fitu.jwt.CustomUserDetails;
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
public class SessionController {
    private final SessionFacade  sessionFacade;

    @Operation(summary = "운동 종료 및 운동 저장 by 조민기")
    @PostMapping("/end")
    public ResponseEntity<SessionEndResponseDTO> endSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("requestDTO") SessionEndRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Long userId = userDetails.getId();

        SessionEndResponseDTO responseDTO = sessionFacade.endSessionWithImage(userId, requestDTO, image);

        return ResponseEntity.ok(responseDTO);
    }
}
