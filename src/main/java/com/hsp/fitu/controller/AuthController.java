package com.hsp.fitu.controller;

import com.hsp.fitu.dto.LoginDTO;
import com.hsp.fitu.dto.LoginResponseDTO;
import com.hsp.fitu.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponseDTO> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        LoginDTO loginDTO = authService.oAuthLogin(accessCode, httpServletResponse);

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder().token(loginDTO.getToken()).build();

        if (loginDTO.isNewUser()) {
            return ResponseEntity.status(201).body(loginResponseDTO);
        } else {
            return ResponseEntity.ok(loginResponseDTO);
        }
    }
}
