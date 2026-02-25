package com.hsp.fitu.repository;

import com.hsp.fitu.dto.ChatRoom;
import com.hsp.fitu.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    @Query("""
            SELECT new com.hsp.fitu.dto.ChatRoom(
                cr.id,
                otherUser.name,
                (   SELECT cm.content
                    FROM ChatMessageEntity cm
                    WHERE cm.chatRoomId = cr.id
                    ORDER BY cm.createdAt DESC
                    LIMIT 1
                ),
                mf.url)
            FROM ChatRoomEntity cr
            JOIN ChatRoomMemberEntity crm ON cr.id = crm.chatRoomId AND crm.userId = :userId
            JOIN ChatRoomMemberEntity otherCrm ON cr.id = otherCrm.chatRoomId AND otherCrm.userId <> :userId
            JOIN UserEntity otherUser ON otherUser.id = otherCrm.userId
            LEFT JOIN MediaFilesEntity mf ON otherUser.profileImgId = mf.id
            """)
    List<ChatRoom> getChatRoomList(@Param("userId") Long userId);

}
