package com.hsp.fitu.repository;

import com.hsp.fitu.entity.PhysicalInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PhysicalInfoRepository extends JpaRepository<PhysicalInfoEntity, Long> {
    PhysicalInfoEntity findFirstByUserIdOrderByRecordedAtDesc(long userId);
    List<PhysicalInfoEntity> findByUserIdAndRecordedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
