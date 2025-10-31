package com.hsp.fitu.repository;

import com.hsp.fitu.entity.ReportsEntity;
import com.hsp.fitu.entity.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminPostReportRepository extends JpaRepository<ReportsEntity, Long> {
    List<ReportsEntity> findByTargetType(TargetType targetType);
}
