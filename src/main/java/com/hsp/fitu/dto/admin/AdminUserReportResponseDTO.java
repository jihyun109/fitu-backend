package com.hsp.fitu.dto.admin;

import java.time.LocalDateTime;

public record AdminUserReportResponseDTO(
        String reportName,
        LocalDateTime reportedAt,
        String universityName
) {}