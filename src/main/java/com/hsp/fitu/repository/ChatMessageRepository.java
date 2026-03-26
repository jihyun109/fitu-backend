package com.hsp.fitu.repository;

import com.hsp.fitu.dto.ChatMessage;
import com.hsp.fitu.entity.ChatMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    /** 최신 N건을 역순(최신→과거)으로 가져온다. Pageable로 limit을 제어한다. */
    @Query("""
            SELECT new com.hsp.fitu.dto.ChatMessage(u.name, cm.content, m.url, cm.createdAt, u.id)
            FROM ChatMessageEntity cm
            JOIN UserEntity u ON cm.senderId = u.id
            LEFT JOIN MediaFilesEntity m ON u.profileImgId = m.id
            WHERE cm.chatRoomId = :chatRoomId
            ORDER BY cm.createdAt DESC
            """)
    List<ChatMessage> findRecentMessages(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable
    );

    /** before 시각 이전의 메시지 N건을 역순으로 가져온다 (이전 메시지 로드용). */
    @Query("""
            SELECT new com.hsp.fitu.dto.ChatMessage(u.name, cm.content, m.url, cm.createdAt, u.id)
            FROM ChatMessageEntity cm
            JOIN UserEntity u ON cm.senderId = u.id
            LEFT JOIN MediaFilesEntity m ON u.profileImgId = m.id
            WHERE cm.chatRoomId = :chatRoomId AND cm.createdAt < :before
            ORDER BY cm.createdAt DESC
            """)
    List<ChatMessage> findMessagesBefore(
            @Param("chatRoomId") Long chatRoomId,
            @Param("before") LocalDateTime before,
            Pageable pageable
    );

    /** 재연결 후 누락된 메시지를 보충할 때 사용. after 이후의 메시지를 시간순으로 반환. */
    @Query("""
            SELECT new com.hsp.fitu.dto.ChatMessage(u.name, cm.content, m.url, cm.createdAt, u.id)
            FROM ChatMessageEntity cm
            JOIN UserEntity u ON cm.senderId = u.id
            LEFT JOIN MediaFilesEntity m ON u.profileImgId = m.id
            WHERE cm.chatRoomId = :chatRoomId AND cm.createdAt > :after
            ORDER BY cm.createdAt
            """)
    List<ChatMessage> findChatMessagesByChatRoomIdAfter(
            @Param("chatRoomId") Long chatRoomId,
            @Param("after") LocalDateTime after
    );
}
