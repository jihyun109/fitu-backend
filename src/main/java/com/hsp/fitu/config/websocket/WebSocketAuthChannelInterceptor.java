package com.hsp.fitu.config.websocket;

import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.jwt.JwtUtil;
import com.hsp.fitu.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

// STOMP CONNECT 프레임에서 JWT 토큰을 검사하고, 인증된 userId를 세션 Principal로 등록
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 클라이언트가 STOMP CONNECT 프레임을 보낼 때만 토큰을 검사
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Authorization 헤더 읽기
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token == null) {
                return null; // CONNECT 거부
            }
            if (token != null && token.startsWith("Bearer ")) token = token.substring(7);

            // jwt 검증
            Claims claims = jwtUtil.validateAndGetClaims(token);

            Long userId = claims.get("userId", Long.class);

            // Principal 설정
            Optional<UserEntity> userEntity = userRepository.findById(userId);

            if (userEntity.isEmpty()) {
                return null;
            } else {
                StompPrincipal principal = new StompPrincipal(String.valueOf(userEntity.get().getId()));
                accessor.setUser(principal);
                accessor.getSessionAttributes().put("userId", userId);
            }

        }
        return message;
    }
}