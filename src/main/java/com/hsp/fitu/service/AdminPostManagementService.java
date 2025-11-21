package com.hsp.fitu.service;

import com.hsp.fitu.dto.admin.AdminPostManagementRequestDTO;
import com.hsp.fitu.dto.admin.AdminPostManagementResponseDTO;
import com.hsp.fitu.dto.PostSliceResponseDTO;

public interface AdminPostManagementService {
    PostSliceResponseDTO<AdminPostManagementResponseDTO> getPostsByUniversity(AdminPostManagementRequestDTO requestDTO, int page, int size);
}
