package com.hsp.fitu.repository;

import com.hsp.fitu.entity.PhysicalInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhysicalInfoRepository extends JpaRepository<PhysicalInfoEntity, Long> {
    PhysicalInfoEntity findFirstByUserIdOrderByRecordedAtDesc(long userId);
}
