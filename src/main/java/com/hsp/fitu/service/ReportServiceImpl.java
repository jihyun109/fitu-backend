package com.hsp.fitu.service;

import com.hsp.fitu.dto.ReportRequestDTO;
import com.hsp.fitu.dto.ReportResponseDTO;
import com.hsp.fitu.entity.ReportsEntity;
import com.hsp.fitu.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;

    @Override
    public ReportResponseDTO report(Long reporterId, ReportRequestDTO reportRequestDTO) {
        ReportsEntity reportsEntity = ReportsEntity.builder()
                .reporterId(reporterId)
                .targetId(reportRequestDTO.targetId())
                .targetType(reportRequestDTO.targetType())
                .build();

        ReportsEntity saved = reportRepository.save(reportsEntity);

        return new ReportResponseDTO(
                saved.getId(),
                saved.getReporterId(),
                saved.getTargetId(),
                saved.getTargetType(),
                saved.getCreatedAt()
        );
    }
}
