package com.hsp.fitu.repository;

import com.hsp.fitu.entity.SessionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionsRepository extends JpaRepository<SessionsEntity, Long> {
    List<SessionsEntity> findByUserIdCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
