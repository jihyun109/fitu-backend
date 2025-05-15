package com.hsp.fitu.controller;

import com.hsp.fitu.dto.AdditionalInfoResponseDTO;
import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/profile")
    public ResponseEntity<AdditionalInfoResponseDTO> inputProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserProfileRequestDTO dto) {
        Long userId = userDetails.getId();
        log.info("userId: " + userId);

        AdditionalInfoResponseDTO additionalInfoResponseDTO = userProfileService.inputProfileOnce(userId, dto);
        return ResponseEntity.ok(additionalInfoResponseDTO);
    }
}