package com.hsp.fitu.controller;

import com.hsp.fitu.dto.LoginDTO;
import com.hsp.fitu.dto.TokenResponseDTO;
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
    public ResponseEntity<TokenResponseDTO> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        LoginDTO loginDTO = authService.oAuthLogin(accessCode, httpServletResponse);

        TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                .token(loginDTO.getToken())
                .userId(loginDTO.getUserId())
                .build();

        if (loginDTO.isNewUser()) {
            return ResponseEntity.status(201).body(tokenResponseDTO);
        } else {
            return ResponseEntity.ok(tokenResponseDTO);
        }
    }

    @GetMapping("/reissue")
    public ResponseEntity<TokenResponseDTO> reissue(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {
        TokenResponseDTO tokenResponseDTO = authService.reissue(refreshToken, response);
        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok("Logout completed.");
    }
}