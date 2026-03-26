package com.hsp.fitu.config;

import com.hsp.fitu.messaging.redis.RedisMessageSubscriber;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * 채팅 메시지 브로커 설정 (Redis Pub/Sub).
 *
 * 브로커를 교체할 때 이 설정 클래스를 대체하고,
 * 새 어댑터(MessageBrokerPort 구현체 + MessageListener)를 등록하면 된다.
 */
@Slf4j
@Configuration
public class ChatMessageBrokerConfig {

    @Bean
    public ChannelTopic chatMessageTopic() {
        return new ChannelTopic("chat:messages");
    }

    /**
     * redis-listener 스레드에서 팬아웃 작업을 분리하기 위한 전용 Executor.
     * redis-listener는 즉시 반환하여 다음 메시지 수신을 놓치지 않는다.
     *
     * CallerRunsPolicy: 큐가 가득 차면 호출 스레드(redis-listener)가 직접 실행한다.
     * 메시지 유실보다 지연 처리가 낫다.
     */
    @Bean
    public Executor broadcastExecutor(MeterRegistry meterRegistry) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("broadcast-");
        executor.setRejectedExecutionHandler((runnable, pool) -> {
            log.warn("broadcastExecutor 큐 포화 — CallerRunsPolicy 적용. poolSize={}, queueSize={}",
                    pool.getPoolSize(), pool.getQueue().size());
            meterRegistry.counter("chat.broadcast.rejected").increment();
            new ThreadPoolExecutor.CallerRunsPolicy().rejectedExecution(runnable, pool);
        });
        executor.initialize();

        // ThreadPoolTaskExecutor 내부의 ThreadPoolExecutor를 직접 참조하여 Gauge 등록
        // ExecutorServiceMetrics.monitor()는 일부 메트릭이 NaN으로 나오는 문제가 있어서
        // 필요한 지표만 직접 Gauge로 등록한다
        ThreadPoolExecutor pool = executor.getThreadPoolExecutor();
        Gauge.builder("chat.broadcast.pool.active", pool, ThreadPoolExecutor::getActiveCount)
                .description("브로드캐스트 스레드풀 활성 스레드 수").register(meterRegistry);
        Gauge.builder("chat.broadcast.pool.size", pool, ThreadPoolExecutor::getPoolSize)
                .description("브로드캐스트 스레드풀 현재 크기").register(meterRegistry);
        Gauge.builder("chat.broadcast.pool.max", pool, e -> e.getMaximumPoolSize())
                .description("브로드캐스트 스레드풀 최대 크기").register(meterRegistry);
        Gauge.builder("chat.broadcast.queue.size", pool, e -> e.getQueue().size())
                .description("브로드캐스트 큐 대기 작업 수").register(meterRegistry);

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
