package com.hsp.fitu.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 1. Enum에서 에러 정보 가져오기
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        // 2. 레코드 생성
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, (String) request.getAttribute("jakarta.servlet.error.request_uri"));

        // 3. 응답 설정
        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json;charset=UTF-8");

        // 4. JSON 변환 후 출력
        String result = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }
}
