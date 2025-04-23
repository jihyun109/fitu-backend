package com.hsp.fitu.controller;

import com.hsp.fitu.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        boolean isNewUser = authService.oAuthLogin(accessCode, httpServletResponse);

        if (isNewUser) {
            return ResponseEntity.status(201).body("카카오 회원가입 완료");
        } else {
            return ResponseEntity.ok("카카오 로그인 완료");
        }
    }
}
