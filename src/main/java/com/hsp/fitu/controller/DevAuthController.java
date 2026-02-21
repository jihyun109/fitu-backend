package com.hsp.fitu.controller;

import com.hsp.fitu.dto.LocalLoginRequestDTO;
import com.hsp.fitu.dto.TokenResponseDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.enums.Role;
import com.hsp.fitu.jwt.JwtUtil;
import com.hsp.fitu.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile("dev")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class DevAuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long refreshExpMs;

    @PostMapping("/login/local")
    public ResponseEntity<TokenResponseDTO> localLogin(@RequestBody LocalLoginRequestDTO request, HttpServletResponse httpServletResponse) {
        String email = request.getEmail();

        UserEntity userEntity = userRepository.findByKakaoEmail(email)
                .orElse(null);

        boolean isNewUser = false;
        if (userEntity == null) {
            isNewUser = true;
            userEntity = UserEntity.builder()
                    .kakaoEmail(email)
                    .build();
            userRepository.save(userEntity);
            userEntity = userRepository.findByKakaoEmail(email).orElse(null);
        }

        Long userId = userEntity.getId();
        Role role = userEntity.getRole() != null ? userEntity.getRole() : Role.USER;
        Long universityId = userEntity.getUniversityId();

        String accessToken = jwtUtil.createAccessToken(userId, role, universityId);
        String refreshToken = jwtUtil.createRefreshToken(userId);

        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshExpMs)
                .sameSite("Lax")
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                .token(accessToken)
                .userId(userId)
                .build();

        if (isNewUser) {
            return ResponseEntity.status(201).body(tokenResponseDTO);
        }
        return ResponseEntity.ok(tokenResponseDTO);
    }
}
