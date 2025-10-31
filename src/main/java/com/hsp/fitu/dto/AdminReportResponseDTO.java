package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record AdminReportResponseDTO(
        long reportId,
        LocalDateTime reportedAt,
        String universityName,
        String postTitle
) {}
