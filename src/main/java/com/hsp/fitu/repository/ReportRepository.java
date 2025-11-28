package com.hsp.fitu.repository;

import com.hsp.fitu.entity.ReportsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportsEntity, Long> {
}
