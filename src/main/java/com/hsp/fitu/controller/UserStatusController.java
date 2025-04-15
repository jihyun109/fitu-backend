package com.hsp.fitu.controller;

import com.hsp.fitu.dto.UserStatusDTO;
import com.hsp.fitu.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/status")

@Slf4j
public class UserStatusController {

    private final UserProfileService userProfileService;

    public UserStatusController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{kakaoEmail}")
    public ResponseEntity<UserStatusDTO> checkUserProfileStatus(@PathVariable String kakaoEmail) {
        UserStatusDTO status = userProfileService.checkUserStatus(kakaoEmail);

        HttpStatus httpStatus = status.isHasProfile() ? HttpStatus.OK : HttpStatus.CREATED;
        
        log.info("카카오 이메일 '{}' 존재 확인 -> {}", kakaoEmail,
                status.isHasProfile() ? "기존 가입자 : 200" : "신규 가입자 : 201");

        return ResponseEntity.status(status.getStatusCode()).body(status);
    }
}
