package com.hsp.fitu.config;

import com.hsp.fitu.messaging.redis.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 채팅 메시지 브로커 설정 (Redis Pub/Sub).
 *
 * 브로커를 교체할 때 이 설정 클래스를 대체하고,
 * 새 어댑터(MessageBrokerPort 구현체 + MessageListener)를 등록하면 된다.
 */
@Configuration
public class ChatMessageBrokerConfig {

    /**
     * 팬아웃 비동기 실행 스레드풀.
     * Redis listener 스레드를 즉시 해방시키고 broadcast() 작업을 위임받는다.
     *
     * 수치 근거:
     *   RedisMessageListenerContainer 기본 dispatch 스레드 = 1개
     *   → core=1: listener가 submit 시 항상 즉시 받아줄 스레드
     *   → max=2: CPU throttle로 broadcast()가 지연될 때 queue 포화 전 추가 스레드
     *   → queue=50: throttle 40ms 구간 최대 적재 ≈ 5개 → 10배 여유
     */
    @Bean(name = "broadcastExecutor")
    public Executor broadcastExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("broadcast-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ChannelTopic chatMessageTopic() {
        return new ChannelTopic("chat:messages");
    }

    /**
     * Redis listener 전용 스레드풀.
     * pub/sub 메시지를 수신하는 스레드 수를 명시적으로 1개로 고정한다.
     * (미설정 시 SimpleAsyncTaskExecutor 사용 → 스레드 수 불확정)
     *
     * Redis 단일 채널은 메시지를 순서대로 전달하므로 1개로 충분하며,
     * broadcastExecutor(core=1, max=2)와 1:1로 대응된다.
     */
    @Bean(name = "redisListenerExecutor")
    public Executor redisListenerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("redis-listener-");
        executor.initialize();
        return executor;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisMessageSubscriber subscriber,
            ChannelTopic chatMessageTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, chatMessageTopic);
        container.setTaskExecutor(redisListenerExecutor());
        return container;
    }
}
