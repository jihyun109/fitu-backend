package com.hsp.fitu.controller;

import com.hsp.fitu.dto.LoginDTO;
import com.hsp.fitu.dto.TokenResponseDTO;
import com.hsp.fitu.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/kakao")
    @Operation(summary = "카카오 로그인")
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
    @Operation(summary = "토큰 재발급")
    public ResponseEntity<TokenResponseDTO> reissue(@CookieValue("refreshToken") String refreshToken) {
        TokenResponseDTO tokenResponseDTO = authService.reissue(refreshToken);

        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        // 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.ok("Logout completed.");
    }
}
