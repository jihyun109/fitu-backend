package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.TargetType;
import lombok.Data;

@Data
public class ReportRequestDTO {
    private Long reporterId;
    private Long targetId;
    private TargetType targetType;
}
