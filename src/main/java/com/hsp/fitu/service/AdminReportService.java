package com.hsp.fitu.service;

import com.hsp.fitu.dto.admin.AdminReportResponseDTO;
import com.hsp.fitu.dto.SliceResponseDTO;

public interface AdminReportService {
    SliceResponseDTO<AdminReportResponseDTO> getReportedPosts(int page, int size);
}
