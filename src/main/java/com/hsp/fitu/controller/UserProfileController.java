package com.hsp.fitu.controller;

import com.hsp.fitu.dto.UserProfileRequestDTO;
import com.hsp.fitu.dto.UserProfileResponseDTO;
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
    public ResponseEntity<UserProfileResponseDTO> inputProfile(@RequestBody UserProfileRequestDTO dto) {
        UserProfileResponseDTO response = userProfileService.inputProfileOnce(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
