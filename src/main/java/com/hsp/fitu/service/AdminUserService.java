package com.hsp.fitu.service;

import com.hsp.fitu.dto.AdminSuspendRequestDTO;
import com.hsp.fitu.dto.AdminUserResponseDTO;

import java.util.List;

public interface AdminUserService {
    List<AdminUserResponseDTO> searchUsersByName(String name);
    void suspendUser(Long userId, AdminSuspendRequestDTO dto);
    void unsuspendUser(Long userId);
}
