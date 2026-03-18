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

    @Bean
    public ChannelTopic chatMessageTopic() {
        return new ChannelTopic("chat:messages");
    }

    /**
     * redis-listener 스레드에서 팬아웃 작업을 분리하기 위한 전용 Executor.
     * redis-listener는 즉시 반환하여 다음 메시지 수신을 놓치지 않는다.
     */
    @Bean
    public Executor broadcastExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("broadcast-");
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
        return container;
    }
}
