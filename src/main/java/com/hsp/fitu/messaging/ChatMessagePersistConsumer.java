package com.hsp.fitu.messaging;

import com.hsp.fitu.entity.ChatMessageEntity;
import com.hsp.fitu.repository.ChatMessageRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Redis Stream에서 채팅 메시지를 꺼내서 DB에 배치 저장하는 Consumer.
 *
 * 3초마다 폴링하여 쌓인 메시지를 한 번에 saveAll()로 INSERT.
 * 메시지 전송 경로(doSendMessage)에서 DB 의존성을 제거하여 CPU를 절감한다.
 *
 * MySQL INSERT 대비 Redis Stream XADD가 가벼운 이유:
 * - MySQL: Spring AOP 프록시 6단계 → 트랜잭션 → JDBC 커넥션 대기 → SQL 파싱 → 디스크 쓰기
 * - Redis: redisTemplate.opsForStream().add() → 메모리 쓰기 (AOP 체인 없음)
 */
@Slf4j
@Component
public class ChatMessagePersistConsumer {

    private static final String STREAM_KEY = "chat:persist:stream";
    private static final String GROUP_NAME = "persist-group";
    private static final int BATCH_SIZE = 200;

    // Pod별로 고유한 Consumer 이름 (같은 Consumer Group에서 메시지를 나눠 처리하기 위함)
    private final String consumerName;
    private final RedisTemplate<String, String> redisTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessagePersistConsumer(
            RedisTemplate<String, String> redisTemplate,
            ChatMessageRepository chatMessageRepository) {
        this.redisTemplate = redisTemplate;
        this.chatMessageRepository = chatMessageRepository;

        // Pod의 호스트이름을 Consumer 이름으로 사용 (K8s에서 Pod 이름 = 호스트이름)
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            hostname = "consumer-" + ProcessHandle.current().pid();
        }
        this.consumerName = hostname;
    }

    /**
     * Consumer Group 생성. 이미 존재하면 무시.
     * Stream이 아직 없으면 MKSTREAM 옵션으로 자동 생성.
     */
    @PostConstruct
    public void createConsumerGroup() {
        try {
            redisTemplate.opsForStream().createGroup(STREAM_KEY, ReadOffset.from("0"), GROUP_NAME);
            log.info("Redis Stream Consumer Group 생성: stream={}, group={}", STREAM_KEY, GROUP_NAME);
        } catch (Exception e) {
            // BUSYGROUP: 이미 존재하면 무시
            if (e.getMessage() != null && e.getMessage().contains("BUSYGROUP")) {
                log.debug("Consumer Group 이미 존재: {}", GROUP_NAME);
            } else {
                log.warn("Consumer Group 생성 실패: {}", e.getMessage());
            }
        }
    }

    /**
     * 3초마다 Redis Stream에서 최대 100건을 읽어 DB에 배치 저장.
     *
     * 흐름:
     * 1. XREADGROUP으로 새 메시지를 최대 BATCH_SIZE건 읽음
     * 2. ChatMessageEntity 리스트로 변환
     * 3. saveAll()로 한 번에 DB INSERT
     * 4. 성공하면 XACK으로 메시지 확인 처리
     * 5. 확인된 메시지는 XDEL로 Stream에서 삭제 (메모리 관리)
     */
    @Scheduled(fixedDelay = 3000)
    public void consumeAndPersist() {
        try {
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                    Consumer.from(GROUP_NAME, consumerName),
                    StreamReadOptions.empty().count(BATCH_SIZE),
                    StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed())
            );

            if (records == null || records.isEmpty()) {
                return;
            }

            // Entity 변환
            List<ChatMessageEntity> entities = records.stream()
                    .map(record -> {
                        Map<Object, Object> fields = record.getValue();
                        return ChatMessageEntity.builder()
                                .chatRoomId(Long.parseLong(fields.get("roomId").toString()))
                                .senderId(Long.parseLong(fields.get("senderId").toString()))
                                .content(fields.get("content").toString())
                                .messageType(ChatMessageEntity.ChatMessageType.TALK)
                                .createdAt(LocalDateTime.parse(fields.get("sendTime").toString()))
                                .build();
                    })
                    .toList();

            // 배치 INSERT
            chatMessageRepository.saveAll(entities);

            // ACK + 삭제
            for (MapRecord<String, Object, Object> record : records) {
                redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP_NAME, record.getId());
                redisTemplate.opsForStream().delete(STREAM_KEY, record.getId());
            }

            log.debug("채팅 메시지 {}건 DB 배치 저장 완료", entities.size());

        } catch (Exception e) {
            log.error("채팅 메시지 배치 저장 실패", e);
            // 실패 시 ACK하지 않았으므로 다음 폴링에서 재처리됨
        }
    }
}
