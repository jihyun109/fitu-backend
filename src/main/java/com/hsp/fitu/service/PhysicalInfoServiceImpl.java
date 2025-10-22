package com.hsp.fitu.service;

import com.hsp.fitu.dto.PhysicalInfoResponseDTO;
import com.hsp.fitu.dto.PhysicalInfoUpdateRequestDTO;
import com.hsp.fitu.dto.PhysicalInfoWeightHeightResponseDTO;
import com.hsp.fitu.dto.PhysicalInfosRequestDTO;
import com.hsp.fitu.entity.PhysicalInfoEntity;
import com.hsp.fitu.repository.PhysicalInfoRepository;
import com.hsp.fitu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhysicalInfoServiceImpl implements PhysicalInfoService {

    private final PhysicalInfoRepository physicalInfoRepository;
    private final UserRepository userRepository;

    @Override
    public PhysicalInfoResponseDTO getPhysicalInfo(long userId) {
        PhysicalInfoEntity physicalInfoEntity = physicalInfoRepository.findFirstByUserIdOrderByRecordedAtDesc(userId);
        String userName = userRepository.findNameById(userId);
        return PhysicalInfoResponseDTO.from(physicalInfoEntity, userName);
    }

    @Override
    public void updatePhysicalInfo(long userId, PhysicalInfoUpdateRequestDTO physicalInfoUpdateRequestDTO) {
        PhysicalInfoEntity physicalInfoEntity = PhysicalInfoEntity.builder()
                .userId(userId)
                .height(physicalInfoUpdateRequestDTO.getHeight())
                .weight(physicalInfoUpdateRequestDTO.getWeight())
                .bodyFat(physicalInfoUpdateRequestDTO.getBodyFat())
                .muscle(physicalInfoUpdateRequestDTO.getMuscle())
                .build();
        physicalInfoRepository.save(physicalInfoEntity);
    }

    @Override
    public List<PhysicalInfoWeightHeightResponseDTO> getMuscleAndBodyFat(long userId, PhysicalInfosRequestDTO physicalInfosRequestDTO) {
        LocalDateTime startDateTime = physicalInfosRequestDTO.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = physicalInfosRequestDTO.getEndDate().plusDays(1).atStartOfDay();
        List<PhysicalInfoEntity> physicalInfoEntities = physicalInfoRepository.findByUserIdAndRecordedAtBetween(userId, startDateTime, endDateTime);

        return PhysicalInfoWeightHeightResponseDTO.from(physicalInfoEntities);
    }
}
