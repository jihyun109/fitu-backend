package com.hsp.fitu.repository;

import com.hsp.fitu.entity.SessionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionsRepository extends JpaRepository<SessionsEntity, Long> {
    List<SessionsEntity> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    Optional<SessionsEntity> findFirstByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    Optional<SessionsEntity> findTopByUserIdAndEndTimeIsNullOrderByIdDesc(Long userId);

}
