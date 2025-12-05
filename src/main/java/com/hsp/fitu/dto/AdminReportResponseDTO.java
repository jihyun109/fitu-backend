package com.hsp.fitu.dto;

import java.util.Date;

public record AdminReportResponseDTO(
        String reportName,
        Date reportedAt,
        String universityName,
        long targetId,
        String postTitle
) {}
