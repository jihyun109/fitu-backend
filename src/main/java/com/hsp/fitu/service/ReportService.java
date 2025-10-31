package com.hsp.fitu.service;

import com.hsp.fitu.dto.ReportRequestDTO;
import com.hsp.fitu.dto.ReportResponseDTO;

public interface ReportService {
    ReportResponseDTO report(Long reporterId, ReportRequestDTO reportRequestDTO);
}
