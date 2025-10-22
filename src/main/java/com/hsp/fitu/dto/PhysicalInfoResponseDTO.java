package com.hsp.fitu.dto;

import com.hsp.fitu.entity.PhysicalInfoEntity;
import com.hsp.fitu.repository.PhysicalInfoRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PhysicalInfoResponseDTO {
    private int height;
    private int weight;
    private int muscle;
    private int bodyFat;
    private String userName;

    public static PhysicalInfoResponseDTO from(PhysicalInfoEntity entity, String userName) {
        return PhysicalInfoResponseDTO.builder()
                .height(entity.getHeight())
                .weight(entity.getWeight())
                .bodyFat(entity.getBodyFat())
                .muscle(entity.getMuscle())
                .userName(userName)
                .build();
    }
}
