package com.hsp.fitu.config;

import com.hsp.fitu.messaging.redis.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

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
