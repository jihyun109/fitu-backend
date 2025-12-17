package com.hsp.fitu.config.websocket;

import com.hsp.fitu.jwt.JwtUtil;
import com.hsp.fitu.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 일반 HTTP 요청으로 변환 가능해야 헤더 접근 가능
        if (!(request instanceof ServletServerHttpRequest)) {
            return false;
        }

        HttpServletRequest servletRequest =
                ((ServletServerHttpRequest) request).getServletRequest();

        // 2) Authorization 헤더에서 토큰 추출
        String authHeader = servletRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7); // "Bearer " 제거

        // 3) 토큰 검증
        Claims claims = jwtUtil.validateAndGetClaims(token);
        if (claims == null) return false;

        Long userId = claims.get("userId", Long.class);

        // 4) userId를 WebSocket 세션에 저장
        userRepository.findById(userId)
                .ifPresent(user -> attributes.put("userId", user.getId()));

        // 5) userId가 저장된 경우에만 연결 허용
        return attributes.containsKey("userId");
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
