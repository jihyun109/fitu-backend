package com.hsp.fitu.controller;

import com.hsp.fitu.dto.UserInfoRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/info")
    public ResponseEntity<String> saveUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails, UserInfoRequestDTO userInfoRequestDTO) {
        Long userId = userDetails.getId();
        userService.saveInfo(userId, userInfoRequestDTO);
        return ResponseEntity.ok().body("success save user info");
    }

}
