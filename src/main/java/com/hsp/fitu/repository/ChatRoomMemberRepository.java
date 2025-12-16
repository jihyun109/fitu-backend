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
}
