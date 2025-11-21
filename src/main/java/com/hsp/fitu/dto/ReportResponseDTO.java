package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.TargetType;

import java.util.Date;

public record ReportResponseDTO(
        long id,
        long reporterId,
        long targetId,
        TargetType targetType,
        Date recordedAt

) {
}
