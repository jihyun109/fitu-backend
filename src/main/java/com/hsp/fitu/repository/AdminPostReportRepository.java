package com.hsp.fitu.repository;

import com.hsp.fitu.dto.AdminReportResponseDTO;
import com.hsp.fitu.entity.ReportsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminPostReportRepository extends JpaRepository<ReportsEntity, Long> {
    @Query("""
    SELECT new com.hsp.fitu.dto.AdminReportResponseDTO(
        u.name,
        r.recordedAt,
        uni.name,
        r.targetId,
        p.title
    )
    FROM ReportsEntity r
    JOIN PostEntity p ON r.targetId = p.id
        AND r.targetType = com.hsp.fitu.entity.enums.TargetType.POST
    JOIN UserEntity u ON p.writerId = u.id
    JOIN UniversityEntity uni ON p.universityId = uni.id
    ORDER BY r.recordedAt DESC
    """)
    Page<AdminReportResponseDTO> findReportedPosts(Pageable pageable);
}
