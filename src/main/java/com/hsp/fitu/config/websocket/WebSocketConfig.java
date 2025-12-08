package com.hsp.fitu.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    // 클라이언트가 최초 WebSocket 연결을 시도할 엔드포인트를 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")    // 웹소켓 연결 주소를 /ws/chat 로 만듦
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOriginPatterns("*")      // cors 허용
                .withSockJS();                      // 웹소켓을 지원하지 않는 브라우저를 위해 SockJS 사용
    }

    // STOMP 메시지가 들어가고, 어디로 나가는 곳을 지정하는 라우팅 규칙 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");         // 클라이언트 → 서버 방향 메시지(prefix)
        registry.enableSimpleBroker("/sub");       // 서버 → 클라이언트 방향 메시지(prefix)
    }
}

