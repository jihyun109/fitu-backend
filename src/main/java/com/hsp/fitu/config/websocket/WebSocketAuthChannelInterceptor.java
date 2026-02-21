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

/**
 * STOMP 채널 인터셉터 — WebSocket 인증 처리.
 *
 * STOMP CONNECT 프레임의 Authorization 헤더에서 JWT를 검증하고,
 * 인증된 userId를 세션 속성과 Principal에 저장한다.
 * 이후 메시지 핸들러(MessageController)에서 세션 속성으로 userId를 꺼내 사용한다.
 */
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 프레임에서만 인증 처리 (SEND, SUBSCRIBE 등은 이미 인증된 세션이므로 통과)
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token == null) {
                return null; // Authorization 헤더 없음 → 연결 거부
            }
            if (token.startsWith("Bearer ")) token = token.substring(7);

            // JWT 서명 검증 및 Claims 추출 (만료·변조 시 예외 발생)
            Claims claims = jwtUtil.validateAndGetClaims(token);
            Long userId = claims.get("userId", Long.class);

            Optional<UserEntity> userEntity = userRepository.findById(userId);
            if (userEntity.isEmpty()) {
                return null; // DB에 존재하지 않는 사용자 → 연결 거부
            }

            // StompPrincipal 등록: convertAndSendToUser() 등에서 사용자 식별에 활용
            StompPrincipal principal = new StompPrincipal(String.valueOf(userEntity.get().getId()));
            accessor.setUser(principal);
            // sessionAttributes에 저장: MessageController에서 @Header로 꺼내 사용
            accessor.getSessionAttributes().put("userId", userId);
        }
        return message;
    }
}