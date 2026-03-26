package com.hsp.fitu.config.websocket;

import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketAuthChannelInterceptorTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MessageChannel channel;

    private WebSocketAuthChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new WebSocketAuthChannelInterceptor(jwtUtil);
    }

    /** STOMP 메시지를 테스트용으로 만드는 헬퍼 */
    private Message<?> createStompMessage(StompCommand command, String authHeader, Map<String, Object> sessionAttrs) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        if (authHeader != null) {
            accessor.addNativeHeader("Authorization", authHeader);
        }
        // sessionAttributes는 항상 설정해야 한다 — 인터셉터가 여기에 값을 저장하기 때문
        accessor.setSessionAttributes(sessionAttrs != null ? sessionAttrs : new HashMap<>());
        accessor.setSessionId("test-session");
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    // ===== CONNECT 테스트 =====

    @Test
    @DisplayName("CONNECT: 유효한 JWT → 연결이 허용된다 (null이 아닌 message 반환)")
    void connect_validToken_returnsMessage() throws Exception {
        // given — Claims를 mock으로 생성
        Claims claims = mock(Claims.class);
        when(claims.get("userId", Long.class)).thenReturn(100L);

        long expiryMillis = System.currentTimeMillis() + 3600000;
        when(jwtUtil.validateAndGetClaims("valid-token")).thenReturn(claims);
        when(jwtUtil.getExpiryMillis(claims)).thenReturn(expiryMillis);

        // sessionAttrs를 직접 전달해서 인터셉터가 저장한 값을 확인할 수 있다
        Map<String, Object> sessionAttrs = new HashMap<>();
        Message<?> message = createStompMessage(StompCommand.CONNECT, "Bearer valid-token", sessionAttrs);

        // when
        Message<?> result = interceptor.preSend(message, channel);

        // then — 연결 허용됨
        assertThat(result).isNotNull();
        // 인터셉터가 sessionAttrs에 userId와 tokenExpiry를 저장했는지 확인
        assertThat(sessionAttrs.get("userId")).isEqualTo(100L);
        assertThat(sessionAttrs.get("tokenExpiry")).isEqualTo(expiryMillis);
    }

    @Test
    @DisplayName("CONNECT: Authorization 헤더 없음 → null 반환 (연결 거부)")
    void connect_noAuthHeader_returnsNull() {
        Message<?> message = createStompMessage(StompCommand.CONNECT, null, null);

        Message<?> result = interceptor.preSend(message, channel);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("CONNECT: 만료된 토큰 → BusinessException(JWT_EXPIRED) 발생")
    void connect_expiredToken_throwsException() throws Exception {
        when(jwtUtil.validateAndGetClaims(anyString()))
                .thenThrow(new BusinessException(ErrorCode.JWT_EXPIRED));

        Message<?> message = createStompMessage(StompCommand.CONNECT, "Bearer expired-token", null);

        // @SneakyThrows로 인해 checked exception이 런타임에 전파됨
        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(BusinessException.class);
    }

    // ===== SEND 테스트 =====

    @Test
    @DisplayName("SEND: 토큰 미만료 → 메시지 통과")
    void send_tokenNotExpired_returnsMessage() {
        long futureExpiry = System.currentTimeMillis() + 3600000;
        Map<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put("userId", 100L);
        sessionAttrs.put("tokenExpiry", futureExpiry);

        when(jwtUtil.isExpired(futureExpiry)).thenReturn(false);

        Message<?> message = createStompMessage(StompCommand.SEND, null, sessionAttrs);

        Message<?> result = interceptor.preSend(message, channel);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("SEND: 토큰 만료됨 → null 반환 (메시지 거부)")
    void send_tokenExpired_returnsNull() {
        long pastExpiry = System.currentTimeMillis() - 3600000;
        Map<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put("userId", 100L);
        sessionAttrs.put("tokenExpiry", pastExpiry);

        when(jwtUtil.isExpired(pastExpiry)).thenReturn(true);

        Message<?> message = createStompMessage(StompCommand.SEND, null, sessionAttrs);

        Message<?> result = interceptor.preSend(message, channel);

        assertThat(result).isNull();
    }

    // ===== SUBSCRIBE 등 다른 명령 테스트 =====

    @Test
    @DisplayName("SUBSCRIBE: 인증/만료 체크 없이 메시지를 그대로 통과시킨다")
    void subscribe_passesThrough() {
        Message<?> message = createStompMessage(StompCommand.SUBSCRIBE, null, new HashMap<>());

        Message<?> result = interceptor.preSend(message, channel);

        assertThat(result).isNotNull();
    }
}
