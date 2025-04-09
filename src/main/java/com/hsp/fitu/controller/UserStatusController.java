package com.hsp.fitu.controller;

import com.hsp.fitu.dto.UserStatusDTO;
import com.hsp.fitu.entity.UserEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserStatusDTO> checkUserProfileStatus(@PathVariable Long id) {
        UserEntity users = userProfileService.getUserById(id);

        boolean hasProfile = users.getHeight() > 0 && users.getWeight() > 0 && users.getGender() != null;

        HttpStatus status = hasProfile ? HttpStatus.OK : HttpStatus.CREATED;

        log.info("User ID {} 프로필 상태 체크 -> 상태 코드: {}", id, status.value());


        return ResponseEntity.status(status).body(new UserStatusDTO(hasProfile));
    }
}
