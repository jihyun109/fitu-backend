package com.hsp.fitu.integration;

import com.hsp.fitu.jwt.JwtTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {
    @InjectMocks
    private JwtTokenService jwtTokenService; // 테스트 대상

    @Test
    void 토큰_추출_성공() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

        // when
        String token = jwtTokenService.extractTokenFromHeader(request);

        // then
        assertEquals("valid-token", token);

    }

    @Test
    void 만료된_토큰_검증_실패() {
        // given
        String expiredToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjc0LCJ1bml2ZXJzaXR5SWQiOjcwMDM2MzMsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzY5MjQ5MjgyLCJleHAiOjE3NjkyNTI4ODJ9.5WKQi3fNOJrC7Xvn6ZeyF1sNg82Igrnyz6dTo8syRMgBSIXzcU7pmrJvX1U4ytV-\"";

        // when & then
        assertThrows(JwtException.class, () -> {
            jwtTokenService.validateToken(expiredToken);
        });
    }
}
