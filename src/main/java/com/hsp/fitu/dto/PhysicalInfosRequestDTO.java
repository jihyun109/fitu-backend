package com.hsp.fitu.dto;

import com.hsp.fitu.entity.PhysicalInfoEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PhysicalInfosRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;

}
