package com.hsp.fitu.service;

import com.hsp.fitu.dto.ReportRequestDTO;
import com.hsp.fitu.dto.ReportResponseDTO;
import com.hsp.fitu.entity.ReportEntity;
import com.hsp.fitu.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;

    @Override
    public ReportResponseDTO report(ReportRequestDTO reportRequestDTO) {
        ReportEntity reportEntity = ReportEntity.builder()
                .reporterId(reportRequestDTO.getReporterId())
                .targetId(reportRequestDTO.getTargetId())
                .targetType(reportRequestDTO.getTargetType())
                .build();

        ReportEntity saved = reportRepository.save(reportEntity);

        return ReportResponseDTO.builder()
                .id(saved.getId())
                .reporterId(saved.getReporterId())
                .targetId(saved.getTargetId())
                .targetType(saved.getTargetType())
                .build();
    }

}
