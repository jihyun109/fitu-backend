package com.hsp.fitu.dto;


import com.hsp.fitu.entity.PhysicalInfoEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class PhysicalInfoWeightHeightResponseDTO {
    private LocalDateTime recordedAt;
    private int bodyFat;
    private int muscle;

    public static List<PhysicalInfoWeightHeightResponseDTO> from(List<PhysicalInfoEntity> entities) {
        return entities.stream()
                .sorted(Comparator.comparing(PhysicalInfoEntity::getRecordedAt))
                .map(entity -> PhysicalInfoWeightHeightResponseDTO.builder()
                        .recordedAt(entity.getRecordedAt())
                        .bodyFat(entity.getBodyFat())
                        .muscle(entity.getMuscle())
                        .build()).collect(Collectors.toList());
    }
}
