package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdminPostManagementRequestDTO;
import com.hsp.fitu.dto.AdminPostManagementResponseDTO;
import com.hsp.fitu.dto.PostSliceResponseDTO;

public interface AdminPostManagementService {
    PostSliceResponseDTO<AdminPostManagementResponseDTO> getPostsByUniversity(AdminPostManagementRequestDTO requestDTO, int page, int size);
}
