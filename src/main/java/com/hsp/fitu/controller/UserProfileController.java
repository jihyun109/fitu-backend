package com.hsp.fitu.controller;

import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.service.UserProfileService;
import org.springframework.http.*;
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
            @RequestParam("userId") long userId,
            @RequestBody UserProfileRequestDTO dto) {
        userProfileService.inputProfileOnce(userId, dto);
        return ResponseEntity.ok("추가정보 입력 완료");
    }
}