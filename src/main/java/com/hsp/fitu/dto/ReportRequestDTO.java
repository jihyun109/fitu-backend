package com.hsp.fitu.dto;

import com.hsp.fitu.entity.enums.TargetType;

public record ReportRequestDTO(
        long targetId,
        TargetType targetType
) {}
