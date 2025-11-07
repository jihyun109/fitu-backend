package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record AdminReportResponseDTO(
        String reportName,
        LocalDateTime reportedAt,
        String universityName,
        long targetId,
        String postTitle
) {}
