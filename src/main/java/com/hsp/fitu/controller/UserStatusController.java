package com.hsp.fitu.controller;

import com.hsp.fitu.dto.UserStatusDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.service.UserProfileService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/status")
public class UserStatusController {

    private final UserProfileService userProfileService;

    public UserStatusController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserStatusDTO> checkUserProfileStatus(@PathVariable Long userId) {
        UserEntity user = userProfileService.getUserById(userId);

        boolean hasProfile = user.getHeight() > 0 && user.getWeight() > 0 && user.getGender() != null;

        HttpStatus status = hasProfile ? HttpStatus.OK : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(new UserStatusDTO(hasProfile));
    }
}
