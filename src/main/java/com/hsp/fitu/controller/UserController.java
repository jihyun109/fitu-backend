package com.hsp.fitu.controller;

import com.hsp.fitu.dto.UserInfoRequestDTO;
import com.hsp.fitu.jwt.CustomUserDetails;
import com.hsp.fitu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/info")
    @Operation(summary = "사용자 추가정보 저장 by 장지현")
    public ResponseEntity<String> saveUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserInfoRequestDTO userInfoRequestDTO) {
        Long userId = userDetails.getId();
        userService.saveInfo(userId, userInfoRequestDTO);
        return ResponseEntity.ok().body("success save user info");
    }

}
