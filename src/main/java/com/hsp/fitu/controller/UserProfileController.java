package com.hsp.fitu.controller;

import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.UserProfileService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/profile")
    public ResponseEntity<String> inputProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserProfileRequestDTO dto) {
        Long userId = userDetails.getId();
        userProfileService.inputProfileOnce(userId, dto);
        return ResponseEntity.ok("추가정보 입력 완료");
    }
}