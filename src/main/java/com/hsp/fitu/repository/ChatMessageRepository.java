package com.hsp.fitu.repository;

import com.hsp.fitu.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
}
