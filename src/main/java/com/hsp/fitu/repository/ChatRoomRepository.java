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
                cr.roomName,
                (   SELECT cm.content
                    FROM ChatMessageEntity cm
                    WHERE cm.chatRoomId = cr.id
                    ORDER BY cm.createdAt DESC
                    LIMIT 1
                ),
                mf.url)
            FROM ChatRoomEntity cr
            JOIN ChatRoomMemberEntity crm ON cr.id = crm.chatRoomId
            LEFT JOIN MediaFilesEntity mf ON cr.thumbnailImgId = mf.id
            WHERE crm.userId = :userId
            """)
    List<ChatRoom> getChatRoomList(@Param("userId") Long userId);

}
