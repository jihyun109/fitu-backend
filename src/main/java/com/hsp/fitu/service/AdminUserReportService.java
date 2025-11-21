package com.hsp.fitu.service;

import com.hsp.fitu.dto.admin.AdminUserReportResponseDTO;
import com.hsp.fitu.dto.SliceResponseDTO;

public interface AdminUserReportService {
    SliceResponseDTO<AdminUserReportResponseDTO> getReportedUser(int page, int size);

}