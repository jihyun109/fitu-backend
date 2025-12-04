package com.hsp.fitu.dto;

import java.util.Date;

public record AdminUserReportResponseDTO(
        String reportName,
        Date reportedAt,
        String universityName
) {}