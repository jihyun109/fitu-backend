package com.hsp.fitu.service;


import com.hsp.fitu.dto.PhysicalInfoResponseDTO;
import com.hsp.fitu.dto.PhysicalInfoUpdateRequestDTO;
import com.hsp.fitu.dto.PhysicalInfoWeightHeightResponseDTO;
import com.hsp.fitu.dto.PhysicalInfosRequestDTO;

import java.util.List;

public interface PhysicalInfoService {
    PhysicalInfoResponseDTO getPhysicalInfo(long userId);
    void updatePhysicalInfo(long userId, PhysicalInfoUpdateRequestDTO physicalInfoUpdateRequestDTO);
    List<PhysicalInfoWeightHeightResponseDTO> getMuscleAndBodyFat(long userId, PhysicalInfosRequestDTO physicalInfosRequestDTO);
}
