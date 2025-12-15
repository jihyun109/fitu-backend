package com.hsp.fitu.repository;

import com.hsp.fitu.entity.ChatRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMemberEntity, Long> {
}
