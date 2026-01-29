package com.hsp.fitu.service;


import com.hsp.fitu.dto.PhysicalInfoResponseDTO;
import com.hsp.fitu.dto.PhysicalInfoUpdateRequestDTO;

public interface PhysicalInfoService {
    PhysicalInfoResponseDTO getPhysicalInfo(long userId);

    void updatePhysicalInfo(long userId, PhysicalInfoUpdateRequestDTO physicalInfoUpdateRequestDTO);

}
