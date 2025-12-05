package com.hsp.fitu.dto;

import java.time.LocalDateTime;

public record AdminPostManagementResponseDTO(
        long id,
        String writerName,
        LocalDateTime createdAt,
        String contents
) {}