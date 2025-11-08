package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record AdminUserReportResponseDTO(
        String reportName,
        LocalDateTime reportedAt,
        String universityName
) {}