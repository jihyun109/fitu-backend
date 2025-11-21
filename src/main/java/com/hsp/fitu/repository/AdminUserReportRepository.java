package com.hsp.fitu.repository;

import com.hsp.fitu.dto.admin.AdminUserReportResponseDTO;
import com.hsp.fitu.entity.ReportsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminUserReportRepository extends JpaRepository<ReportsEntity, Long> {
    @Query("""
    SELECT new com.hsp.fitu.dto.AdminUserReportResponseDTO(
        u.name,
        r.recordedAt,
        uni.name
    )
    FROM ReportsEntity r
    JOIN PostEntity p ON r.targetId = p.id
        AND r.targetType = com.hsp.fitu.entity.enums.TargetType.USER
    JOIN UserEntity u ON p.writerId = u.id
    JOIN UniversityEntity uni ON p.universityId = uni.id
    ORDER BY r.recordedAt DESC
    """)
    Page<AdminUserReportResponseDTO> findReportedUser(Pageable pageable);
}
