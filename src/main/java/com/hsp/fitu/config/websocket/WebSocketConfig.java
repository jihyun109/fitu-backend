package com.hsp.fitu.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket(STOMP) 전반 설정.
 * 연결 엔드포인트, 메시지 라우팅 prefix, 채널 인터셉터를 등록한다.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor;

    /**
     * 클라이언트가 WebSocket 연결을 맺을 엔드포인트 등록.
     * (HTTP Handshake 단계 인증은 현재 비활성화 — STOMP CONNECT에서 처리)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
//                .addInterceptors(webSocketAuthInterceptor)  // HTTP Handshake 단계 인증 (현재 미사용)
                .setAllowedOriginPatterns("*");
    }

    /**
     * STOMP 메시지 라우팅 규칙 설정.
     * /pub/** : 클라이언트 → 서버 (MessageController의 @MessageMapping과 매핑)
     * /sub/** : 서버 → 클라이언트 (클라이언트가 subscribe할 목적지 prefix)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }

    /**
     * 인바운드 채널에 JWT 인증 인터셉터 등록.
     * STOMP CONNECT 프레임 수신 시 WebSocketAuthChannelInterceptor가 먼저 실행된다.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthChannelInterceptor);
        // 0.6 vCPU 제한 환경에서 스레드 폭발 방지
        // core=4: 평상시 처리 (0.6코어 기준 적정)
        // max=8: 연결 폭증 구간 처리 (2× 여유)
        // queue=200: 150 VU 버스트 버퍼
        registration.taskExecutor()
                .corePoolSize(4)
                .maxPoolSize(8)
                .queueCapacity(200);
    }

    /**
     * 아웃바운드 채널 스레드풀 설정.
     * broadcast() 팬아웃 시 convertAndSend 호출량(방 인원 수 × 메시지 수)을 처리.
     * inbound보다 큐를 크게 설정 — 팬아웃 태스크가 순간 대량 적재되기 때문.
     *
     * 모니터링 목적:
     *   설정 시 Spring Boot Actuator가 자동으로 executor 메트릭 노출
     *   → Prometheus executor_queued_tasks{name="clientOutboundChannel-N"} 로 팬아웃 병목 관찰 가능
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // core=4: 평상시 클라이언트 전송 처리
        // max=16: 팬아웃 burst 시 병렬 전송 (inbound보다 높게 — 전송 태스크가 빠르고 많음)
        // queue=1000: 팬아웃 적재 버퍼 (방 인원 × 동시 메시지 수 흡수용)
        registration.taskExecutor()
                .corePoolSize(4)
                .maxPoolSize(16)
                .queueCapacity(1000);
    }
}

