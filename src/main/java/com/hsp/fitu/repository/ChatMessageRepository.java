package com.hsp.fitu.repository;

import com.hsp.fitu.dto.ChatMessage;
import com.hsp.fitu.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    @Query("""
            SELECT new com.hsp.fitu.dto.ChatMessage(u.name, cm.content, m.url, cm.createdAt, u.id)
            FROM ChatMessageEntity cm
            JOIN UserEntity u ON cm.senderId = u.id
            LEFT JOIN MediaFilesEntity m ON u.profileImgId = m.id
            WHERE cm.chatRoomId = :chatRoomId
            ORDER BY cm.createdAt
            """)
    List<ChatMessage> findChatMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);

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
