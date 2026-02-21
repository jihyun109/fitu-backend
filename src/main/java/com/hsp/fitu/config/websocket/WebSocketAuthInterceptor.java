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

/**
 * HTTP Handshake 단계의 WebSocket 인증 인터셉터.
 *
 * WebSocket 연결 수립 전 HTTP Upgrade 요청에서 JWT를 검증한다.
 * 현재 WebSocketConfig에서 등록이 비활성화되어 있으며,
 * 인증은 STOMP CONNECT 단계의 WebSocketAuthChannelInterceptor가 담당한다.
 */
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    /**
     * WebSocket Handshake 전 실행. false 반환 시 연결이 거부된다.
     * SockJS 환경에서는 HTTP 요청 타입이 다를 수 있으므로 먼저 타입을 확인한다.
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // SockJS 등 non-servlet 요청은 헤더 접근 불가 → 거부
        if (!(request instanceof ServletServerHttpRequest)) {
            return false;
        }

        HttpServletRequest servletRequest =
                ((ServletServerHttpRequest) request).getServletRequest();

        String authHeader = servletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);

        Claims claims = jwtUtil.validateAndGetClaims(token);
        if (claims == null) return false;

        Long userId = claims.get("userId", Long.class);

        // 검증된 userId를 WebSocket 세션 attributes에 저장
        // (이후 핸들러에서 attributes로 접근 가능)
        userRepository.findById(userId)
                .ifPresent(user -> attributes.put("userId", user.getId()));

        return attributes.containsKey("userId");
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Handshake 이후 별도 처리 없음
    }
}
