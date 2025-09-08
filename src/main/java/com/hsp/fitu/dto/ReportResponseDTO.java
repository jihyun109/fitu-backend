package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.TargetType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportResponseDTO {
    private Long id;
    private Long reporterId;
    private Long targetId;
    private TargetType targetType;
}
