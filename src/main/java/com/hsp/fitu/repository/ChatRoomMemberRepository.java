package com.hsp.fitu.repository;

import com.hsp.fitu.entity.ChatRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMemberEntity, Long> {
    @Query("""
            SELECT c.userId
            FROM ChatRoomMemberEntity c
            WHERE c.chatRoomId = :chatRoomId
            """)
    List<Long> findAllUserIdsByChatRoomId(@Param("chatRoomId")Long chatRoomId);

    @Query("""
            SELECT c1.chatRoomId
            FROM ChatRoomMemberEntity c1
            JOIN ChatRoomMemberEntity c2 ON c1.chatRoomId = c2.chatRoomId
            WHERE c1.userId = :userId1 AND c2.userId = :userId2
            AND (SELECT COUNT(c3) FROM ChatRoomMemberEntity c3 WHERE c3.chatRoomId = c1.chatRoomId) = 2
            """)
    List<Long> findDirectChatRoomBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
