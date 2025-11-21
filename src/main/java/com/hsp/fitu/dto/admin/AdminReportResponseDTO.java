package com.hsp.fitu.dto.admin;

import java.time.LocalDateTime;

public record AdminReportResponseDTO(
        String reportName,
        LocalDateTime reportedAt,
        String universityName,
        long targetId,
        String postTitle
) {}
